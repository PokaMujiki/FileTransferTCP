package com.networkcourse.transferprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TransferProtocol {
    private static final String SERVER_SUCCESS_REPLY = "Success!";
    private static final String SERVER_FAILURE_REPLY = "Failure!";
    public static final int BUFFER_SIZE = 1024;

    /**
     * 2 bytes(file name length) + file name -  send using DataOutputStream.writeUTF
     * 8 bytes - file size
     */
    public static void sendHeaders(File file, DataOutputStream output) throws IOException {
        output.writeUTF(file.getPath());
        output.writeLong(Files.size(Paths.get(file.getAbsolutePath())));
    }

    public static DownloadingFileInfo recvHeader(DataInputStream input) throws IOException {
        String fileName = input.readUTF();
        long fileSize =  input.readLong();
        return new DownloadingFileInfo(fileName, fileSize);
    }

    public static void sendEndDownloadStatus(DataOutputStream output, long planedFileSize, long actualGotFileSize)
            throws IOException {
        if (planedFileSize == actualGotFileSize) {
            output.writeUTF(SERVER_SUCCESS_REPLY);
        }
        else {
            output.writeUTF(SERVER_FAILURE_REPLY);
            output.close();
            throw new IOException("Expected file size " + planedFileSize + ", but got " + actualGotFileSize);
        }
    }

    public static boolean recvServerEndDownloadStatus(DataInputStream input) throws IOException {
        return SERVER_SUCCESS_REPLY.equals(input.readUTF());
    }
}