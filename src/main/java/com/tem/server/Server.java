package com.tem.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Server implements Runnable {

    private Socket socket;
    private InputStreamReader streamReader;
    private PrintWriter out;
    private BufferedReader in;

    private State state;

    enum State{
        Authorisation,
        PwdWaiting,
        Transaction
    }

    Server(Socket socket, int timeout) throws SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(timeout * 1000);
        state = State.Authorisation;
        System.out.println("[" + socket.getInetAddress() + "] " + "Just connected" );
    }

    @Override
    public void run() {

        try {
            streamReader = new InputStreamReader(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(streamReader);
            String input;
            StringBuilder output = new StringBuilder("-ERR unknown error");
            User user = null;
            out.println("+OK POP3 server ready");

            while ((input = in.readLine()) != null) {
                System.out.println(input);
                switch (state) {
                    case Authorisation:
                        System.out.println("user :" + input);
                        if (input.startsWith("USER")) {
                            String username = input.split(" ")[1];
                            user = User.getUser(username);
                            if (user == null || user.getLock()) {
                                output = new StringBuilder("-ERR never heard of mailbox name");
                            } else {
                                output = new StringBuilder("+OK name is a valid mailbox");
                                state = State.PwdWaiting;
                            }
                        }
                        break;
                    case PwdWaiting:
                        if (input.startsWith("PASS")) {
                            if (user == null) {
                                System.err.println("ERR unable to lock maildrop");
                            }
                            String pwd = input.split(" ")[1];
                            assert user != null;
                            if (user.getPassword().equals(pwd)) {
                                output = new StringBuilder("+OK maildrop locked and ready");
                                state = State.Transaction;
                                user.setLock(true);
                            } else {
                                output = new StringBuilder("-ERR invalid password");
                            }
                        }
                        break;
                    case Transaction:
                        if (input.startsWith("QUIT")) {
                            assert user != null;
                            user.setLock(false);
                            output = new StringBuilder("");
                            state = State.Authorisation;
                        } else if (input.startsWith("NOOP")) {
                            output = new StringBuilder("+OK");
                        } else if (input.startsWith("STAT")) {
                            Integer sum = 0, countMessages = 0;
                            assert user != null;
                            for (Mail mail : user.getMails()) {
                                sum += mail.getSize();
                                countMessages++;
                            }
                            output = new StringBuilder(" +OK " + countMessages + " " + sum);
                        } else if (input.startsWith("RETR")) {
                            Integer id = Integer.parseInt(input.split(" ")[1]);
                            assert user != null;
                            for (Mail mail : user.getMails()) {
                                if (mail.getMessageId().equals(id)) {
                                    output = new StringBuilder("+OK message follows\n" + mail.getMessageId() + " " + mail.getSize() + "\n");
                                    output.append("From : ").append(mail.getFromName()).append(" <").append(mail.getFromAdress()).append(">\n");
                                    output.append("To : ").append(mail.getUser().getUsername()).append(" <").append(mail.getUser().getAddress()).append(">\n");
                                    output.append("Subject : ").append(mail.getObject()).append("\n");
                                    output.append("Date : ").append(mail.getDate().toString()).append("\n");
                                    output.append("Id : ").append(mail.getMessageId()).append("\n");
                                    output.append(mail.getContent());
                                    break;
                                }
                            }
                        } else if (input.startsWith("LIST")) {
                            assert user != null;
                            output = new StringBuilder("+OK scan listing follows\n" + user.getMails().size());
                            for (Mail mail : user.getMails()) {
                                output.append("\n").append(mail.getMessageId()).append(" ").append(mail.getSize());
                            }
                            output.append("\n.");
                        }
                        break;
                }
                out.println(output);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[" + socket.getInetAddress() + "] " + "Socket Timeout");
        } catch (IOException e) {
            System.err.println("Stream Error");
        } finally {
            try {
                streamReader.close();
                in.close();
                out.close();
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("[" + socket.getInetAddress() + "] " + "User Disconnected");
        }
    }
}
