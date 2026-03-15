package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import bg.sofia.uni.fmi.mjt.retriever.CryptoCurrencyRetriever;
import bg.sofia.uni.fmi.mjt.thread.CommandWorkerThread;
import bg.sofia.uni.fmi.mjt.response.ClientResponse;

import bg.sofia.uni.fmi.mjt.repository.InMemoryWalletManager;
import bg.sofia.uni.fmi.mjt.thread.StopServerDaemonThread;
import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.utility.UncaughtExceptionHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static bg.sofia.uni.fmi.mjt.utility.FileHandler.intializeReader;

public class WalletManagerServer {

    private static final int SERVER_PORT = 9685;
    private static final String SERVER_HOST = "0.0.0.0";
    private static final Gson GSON = new Gson();
    private static final ExecutorService APP_EXECUTOR = Executors.newFixedThreadPool(15);
    private static final int BUFFER_SIZE = 4096;
    private static final Path CACHE_FILE = Path.of("out", "cache.txt");
    private static final Path CRASH_FILE = Path.of("out", "log.txt");
    private static final Path WALLET_MANAGER = Path.of("out", "data.txt");

    private static final String LISTENING = " and listening on port ";
    private static final String STARTED_ON = "NIO server started on ";
    private static boolean running = true;
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final WalletManagerRepository repository;

    private String user = null;

    static   void main() {
        startServer();
    }

    public WalletManagerServer(WalletManagerRepository repository) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            this.repository = repository;
        } catch (IOException e) {
            throw new UncheckedIOException("The app failed to start server contact support", e);
        }
    }

    public void start() {
        try {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println(STARTED_ON + serverAddress.getHostAddress()
                + LISTENING + SERVER_PORT);
            while (running) {
                Iterator<SelectionKey> iterator = getSelectionKeyIterator();

                if (iterator == null) continue; // select() is blocking but may still return 0
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        acceptClient(key);
                    } else if (key.isReadable()) {
                        processSelectionKey(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("The app encountered an error on the server side contact support", e);
        }
    }

    private Iterator<SelectionKey> getSelectionKeyIterator() throws IOException {
        int readyChannels = selector.select();
        if (readyChannels == 0) {
            return null;
        }
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        return selectedKeys.iterator();
    }

    public void stop() {
        running = false;
        selector.wakeup();
        APP_EXECUTOR.shutdown();
    }

    public static void prepareConsoleOnShutdown(WalletManagerServer server, WalletManagerRepository repository) {
        Thread consoleThread = new Thread(new StopServerDaemonThread(server, repository));
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    private static void startServer() {
        WalletManagerRepository repository;
        try (Reader walletManagerReader = intializeReader(WALLET_MANAGER);
             Reader cacheFileReader  = intializeReader(CACHE_FILE)) {
            repository = new InMemoryWalletManager(walletManagerReader,
                    new CacheCrypto(cacheFileReader, CryptoCurrencyRetriever.createDefault()));
        } catch (IOException e) {
            throw new UncheckedIOException("Server aborted because of a problem when loading information", e);

        }
        WalletManagerServer server = new WalletManagerServer(repository);

        UncaughtExceptionHandler.setThreadToHandleExceptionBeforeTermination(repository, CRASH_FILE, server);
        prepareConsoleOnShutdown(server, repository);

        server.start();
    }

    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        // In non-blocking mode accept() may return null if no connection is ready
        if (clientChannel == null) {
            return;
        }

        clientChannel.configureBlocking(false);

        // Allocate one buffer per connection
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        // Attach per-client state (here: this connection's buffer) to the selection key
        clientChannel.register(selector, SelectionKey.OP_READ, buffer);

        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private String getClientInput(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        buffer.clear();
        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);
        ClientResponse response = GSON.fromJson(
            new String(clientInputBytes, StandardCharsets.UTF_8), ClientResponse.class);
        user = response.user();
        return response.command();
    }

    private void processSelectionKey(SelectionKey key) throws IOException {
        String clientInput = getClientInput(key);
        Future<String> output = APP_EXECUTOR.submit(new CommandWorkerThread(clientInput, user, repository));
        if (clientInput == null) {
            return;
        }
        try {
            writeClientOutput(key, output.get());
            output.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Request processing was interrupted. The server is " +
                "shutting down or overloaded", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("The server failed to process the request due to an internal error"
                , e.getCause());
        }

    }

    private void writeClientOutput(SelectionKey key, String output) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private String getClientInputFromBigRequest(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        StringBuilder sb = new StringBuilder();
        while (true) {
            buffer.clear();
            int readBytes = clientChannel.read(buffer);
            if (readBytes == -1) {
                clientChannel.close();
                return null;
            }
            if (readBytes == 0) {
                break; // no more data for now
            }

            buffer.flip();
            sb.append(StandardCharsets.UTF_8.decode(buffer));
        }

        String json = sb.toString();
        if (json.isEmpty()) {
            return null;
        }

        ClientResponse response = GSON.fromJson(json, ClientResponse.class);
        user = response.user();
        return response.command();
    }

}
