package com.networkcourse.server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int BACKLOG = 50;
    private static final String SERVER_IP = "127.0.0.1";
    private final static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static void downloadFile(Socket socket) {
        try {
            new FileReceiver(socket).recvFile();
        }
        catch (IOException e) {
            try {
                socket.close();
            }
            catch (IOException ignored) { }
            System.out.println("IOException downloading file. Downloading is canceled!");
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
        }
        catch (InterruptedException ignored) { }
    }

    public static void main(String[] args) {
        int port;
        try {
            if (args.length != 1) {
                throw new IllegalArgumentException();
            }
            port = Integer.parseInt(args[0]);
            if (port < 0 || port > 65_353) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            System.err.println("Invalid argument for port!");
            return;
        }
        catch (IllegalArgumentException e) {
            System.err.println("Wrong amount of arguments!");
            return;
        }

        try (var serverSocket = new ServerSocket(port, BACKLOG, InetAddress.getByName(SERVER_IP))) {
            System.out.println("Server started");
            while (!serverSocket.isClosed()) {
                var connection = serverSocket.accept();
                threadPool.execute(() -> downloadFile(connection));
                System.out.println("New connection");
            }
        }
        catch (IOException e) {
            System.err.println("Can't start server socket!");
        }
        finally {
            threadPool.shutdown();
        }
    }
}