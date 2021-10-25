package com.networkcourse.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final int BACKLOG = 50;
    private static final String SERVER_IP = "127.0.0.1";

    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final int port;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public Server(int port) {
        this.port = port;
    }

    public void launch() {
        try (var serverSocket = new ServerSocket(port, BACKLOG, InetAddress.getByName(SERVER_IP))) {
            LOGGER.info("Server started");
            while (!serverSocket.isClosed()) {
                var connection = serverSocket.accept();
                threadPool.execute(() -> downloadFile(connection));
                LOGGER.info("New connection");
            }
        }
        catch (IOException e) {
            LOGGER.error("Can't start server socket!");
        }
        finally {
            threadPool.shutdown();
        }
    }

    private static void downloadFile(Socket socket) {
        try {
            new FileReceiver(socket).recvFile();
        }
        catch (IOException e) {
            try {
                socket.close();
            }
            catch (IOException ignored) { }
            String message = "IOException downloading file. Downloading is canceled!";
            if (e.getMessage() != null) {
                message += e.getMessage();
            }
            LOGGER.error(message);
        }
        catch (InterruptedException ignored) { }
    }
}