package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.response.ClientResponse;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.utility.UncaughtExceptionHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public class WalletManagerClient {

    private static final int SERVER_PORT = 9685;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4096;
    private static final Gson GSON = new Gson();
    private static final Path CRASH_FILE = Path.of("out", "crash.log");
    private static final String CONNECTED_TO_THE_SERVER = "Connected to the server.";
    private static final String DISCONNECT = "disconnect";
    private String user = null;

    static void main() {
        UncaughtExceptionHandler.setThreadToHandleExceptionBeforeClientTermination(CRASH_FILE);
        WalletManagerClient client = new WalletManagerClient();
        client.start();
    }

    public void start() {

        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println(CONNECTED_TO_THE_SERVER);

            while (true) {
                String command = scanner.nextLine();

                if (command.equals(DISCONNECT)) {
                    break;
                }
                startRequest(buffer, command);
                processResponse(buffer, socketChannel);
            }

        } catch (IOException e) {
            throw new UncheckedIOException("There was a problem in the communication with the server.", e);
        }
    }

    private void processResponse(ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        buffer.flip(); // switch to reading mode
        socketChannel.write(buffer); // buffer drain

        buffer.clear(); // switch to writing mode
        socketChannel.read(buffer); // buffer fill
        buffer.flip(); // switch to reading mode

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        buffer.clear();
        String reply = new String(byteArray, StandardCharsets.UTF_8); // buffer drain
        ServerResponse response = GSON.fromJson(reply, ServerResponse.class);
        System.out.println(response.message());

        if (response.user() != null) {
            user = response.user();
        }
    }

    private void startRequest(ByteBuffer buffer, String command) {
        buffer.clear(); // switch to writing mode
        buffer.put(GSON.toJson(new ClientResponse(command, user)).getBytes());
    }
}