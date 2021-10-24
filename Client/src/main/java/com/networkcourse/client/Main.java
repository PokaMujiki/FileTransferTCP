package com.networkcourse.client;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.file.NoSuchFileException;

public class Main {
    public static void main(String[] args) {
        File file;
        InetAddress serverIpAddress;
        int serverPort;
        try {
            if (args.length != 3) {
                throw new IllegalArgumentException();
            }

            serverPort = Integer.parseInt(args[2]);
            if (serverPort < 0 || serverPort > 65_353) {
                throw new NumberFormatException();
            }
            serverIpAddress = Inet4Address.getByName(args[1]);

            file = new File(args[0]);
            if (!file.exists()) {
                throw new NoSuchFileException(args[0]);
            }
        }
        catch (NoSuchFileException e) {
            System.err.println("No such file \"" + e.getFile() + "\"!");
            return;
        }
        catch (NumberFormatException e) {
            System.err.println("Invalid argument for port!");
            return;
        }
        catch (IllegalArgumentException e) {
            System.err.println("Wrong amount of arguments!");
            return;
        }
        catch (IOException e) {
            System.err.println("Invalid server ip argument!");
            return;
        }

        try {
            FileSender sender = new FileSender(serverIpAddress, serverPort);
            if (sender.sendFile(file)) {
                System.out.println("Success!");
            }
            else {
                System.out.println("Failure!");
            }
        }
        catch (IOException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            else {
                System.out.println("Error sending file");
            }
        }
    }
}