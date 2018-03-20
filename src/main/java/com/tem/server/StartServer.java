package com.tem.server;

import java.net.ServerSocket;

public class StartServer {

    private StartServer() {
        final int SERVER_PORT = 1110;
        final int TIMEOUT = 600;

        new User("Artem", "123");
        new User("Kate", "123");
        try {
            ServerSocket socket = new ServerSocket(SERVER_PORT);
            while (true) {
                Server server = new Server(socket.accept(), TIMEOUT);
                Thread thread = new Thread(server);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static void main(String [] args) {
        StartServer startServer = new StartServer();
    }
}
