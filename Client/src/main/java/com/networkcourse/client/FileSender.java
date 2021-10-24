package com.networkcourse.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import com.networkcourse.transferprotocol.TransferProtocol;

public class FileSender {
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public FileSender(InetAddress serverIp, int serverPort) throws IOException {
        try {
            socket = new Socket(serverIp, serverPort);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            throw new IOException("Can't open socket. Probably server is down");
        }
    }

    public boolean sendFile(File file) throws IOException {
        // send header
        TransferProtocol.sendHeaders(file, output);

        //send file content
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[TransferProtocol.BUFFER_SIZE];
        int bytesRead = 0;
        while ((bytesRead = bufferedInputStream.read(buf)) > 0) {
            output.write(buf, 0, bytesRead);
        }
        bufferedInputStream.close();
        socket.shutdownOutput();

        // get server end of download reply
        return TransferProtocol.recvServerEndDownloadStatus(input);
    }
}