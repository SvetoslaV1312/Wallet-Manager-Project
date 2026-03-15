package bg.sofia.uni.fmi.mjt.thread;

import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import bg.sofia.uni.fmi.mjt.server.WalletManagerServer;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class StopServerDaemonThread implements Runnable {
    private static final String SHUTTING_DOWN_SERVER = "Shutting down server...";
    private static final String STOP = "stop";
    private final WalletManagerServer server;
    private final WalletManagerRepository repository;

    public StopServerDaemonThread(WalletManagerServer server, WalletManagerRepository repository) {
        this.server = server;
        this.repository = repository;
    }

    @Override
    public void run() {
        Thread savingThread = new Thread(new SaveDBThread(repository));
        savingThread.setDaemon(false);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String line;
                try {
                    line = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    startSavingProcess(savingThread);
                    break;
                }
                if (line.equals(STOP)) {
                    startSavingProcess(savingThread);
                    break;
                }
            }
        }
    }

    private void startSavingProcess(Thread savingThread) {
        savingThread.start();
        System.out.println(SHUTTING_DOWN_SERVER);
        server.stop();
    }
}
