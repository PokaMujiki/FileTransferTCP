package com.networkcourse.server;

public class Main {
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

        Server server = new Server(port);
        server.launch();
    }
}