package com.tem.server;

import java.net.ServerSocket;
import java.util.Date;
import java.util.Objects;

public class StartServer {

    public static void main(String [] args) {
        final int SERVER_PORT = 1110;
        final int TIMEOUT = 600;

        new User("Artem", "123");
        initMails();
        try {
            try (ServerSocket socket = new ServerSocket(SERVER_PORT)) {
                while (true) {
                    Server server = new Server(socket.accept(), TIMEOUT);
                    Thread thread = new Thread(server);
                    thread.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    private static void initMails(){
        Mail mail = new Mail(Objects.requireNonNull(User.getUser("Artem")));
        mail.setContent("This is a message to say hello.");
        mail.setDate(new Date());
        mail.setFromAdress("@gmail.com");
        mail.setFromName("Artem");
        mail.setMessageId(1);
        mail.setObject("The test message");
    }

}
