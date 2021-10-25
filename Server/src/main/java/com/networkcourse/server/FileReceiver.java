package com.networkcourse.server;

import com.networkcourse.utils.FileUtils;
import com.networkcourse.transferprotocol.TransferProtocol;
import com.networkcourse.transferprotocol.DownloadingFileInfo;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReceiver {
    private static final String DEFAULT_DIR_WITH_SEPARATOR = "uploads" + File.separator;
    private static final long DELAY_PRINT_SPEED = 3_000;
    private static final long PERIOD_PRINT_SPEED = 3_000;
    private static final int INFO_MAX_LENGTH = 1000;
    private static final int MILLISECS_IN_SEC = 1000;
    private static final int BYTES_IN_KB = 1024;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReceiver.class);

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    private long bytesReceived = 0;
    private long bytesReceivedPeriod = 0;
    private final Object objectToSynchronizePeriodCounter = new Object();

    private Instant startTime;
    private Instant periodStartTime;


    public FileReceiver(Socket socket) throws IOException {
        // try to create uploads folder if it's not created yet
        File uploads = new File(DEFAULT_DIR_WITH_SEPARATOR);
        if (!uploads.exists() && !uploads.mkdir()) {
            throw new IOException("Failed to create \"" + DEFAULT_DIR_WITH_SEPARATOR +
                    "\" folder for downloading files");
        }

        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public void recvFile() throws IOException, InterruptedException {
        // receive header from client
        DownloadingFileInfo info = TransferProtocol.recvHeader(input);
        File downloadingFile = FileUtils.createFile(DEFAULT_DIR_WITH_SEPARATOR + info.getFileName());

        // receive file from client
        var speedCounter = Executors.newSingleThreadScheduledExecutor();
        startTime = Instant.now();
        periodStartTime = startTime;
        speedCounter.scheduleAtFixedRate(() -> printSpeed(downloadingFile), DELAY_PRINT_SPEED, PERIOD_PRINT_SPEED,
                TimeUnit.MILLISECONDS);

        byte[] buffer = new byte[TransferProtocol.BUFFER_SIZE];
        try (FileOutputStream fileDataOutputStream = new FileOutputStream(downloadingFile)) {
            int bytesRead;
            while ((bytesRead = input.read(buffer)) > 0) {
                fileDataOutputStream.write(buffer, 0, bytesRead);
                synchronized (objectToSynchronizePeriodCounter) {
                    bytesReceived += bytesRead;
                    bytesReceivedPeriod += bytesRead;
                }
            }
        }
        finally {
            if (startTime.equals(periodStartTime)) {
                Thread.sleep(PERIOD_PRINT_SPEED);
            }
            speedCounter.shutdown();
        }

        // send download status to client
        TransferProtocol.sendEndDownloadStatus(output, info.getFileSize(), bytesReceived);
        socket.shutdownOutput();

        LOGGER.info("File " + downloadingFile.getPath() + " downloaded");
    }

    private void printSpeed(File downloadingFile) {
        synchronized (objectToSynchronizePeriodCounter) {
            Instant timeNow = Instant.now();
            StringBuilder downloadInfo = new StringBuilder(INFO_MAX_LENGTH);
            downloadInfo.append("---")
                    .append(downloadingFile.getPath())
                    .append("---\n")
                    .append("Last period speed: ")
                    .append(bytesReceivedPeriod /
                            (Duration.between(periodStartTime, timeNow).toMillis() / MILLISECS_IN_SEC) / BYTES_IN_KB)
                    .append("Kb/s\nAverage speed \t: ")
                    .append(bytesReceived /
                            (Duration.between(startTime, timeNow).toMillis() / MILLISECS_IN_SEC) / BYTES_IN_KB)
                    .append("Kb/s\n")
                    .append("------");
            System.out.println(downloadInfo);
            bytesReceivedPeriod = 0;
            periodStartTime = timeNow;
        }
    }
}