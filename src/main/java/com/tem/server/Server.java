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
        Closed,
        Authorisation,
        PwdWaiting,
        Transaction
    }

    Server(Socket socket, int timeout) throws SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(timeout * 1000);
        state = State.Authorisation;
		/* Server connection message */
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

            label:
            while ((input = in.readLine()) != null) {
                System.out.println(input);
                switch (state) {
                    case Closed:
                        break;
                    case Authorisation:
                        System.out.println("user :" + input);
                        if (input.startsWith("USER")) {
                            String username = input.split(" ")[1];
                            user = User.getUser(username);
                            if (user == null) {
                                output = new StringBuilder("-ERR username not recognized");
                            } else if (user.getLock()) {
                                output = new StringBuilder("-ERR user is already in use");
                            } else {
                                output = new StringBuilder("+OK Waiting for password");
                                state = State.PwdWaiting;
                            }
                        }
                        break;
                    case PwdWaiting:
                        if (input.startsWith("PASS")) {
                            if (user == null) {
                                System.err.println("User is null, impossible");
                            }
                            String pwd = input.split(" ")[1];
                            assert user != null;
                            if (user.getPassword().equals(pwd)) {
                                output = new StringBuilder("+OK Password is correct, logged in");
                                state = State.Transaction;
                                user.setLock(true);
                            } else {
                                output = new StringBuilder("-ERR wrong password");
                            }
                        }
                        break;
                    default:
                        if (input.startsWith("QUIT")) {
                            assert user != null;
                            user.setLock(false);
                            break label;
                        } else if (input.startsWith("APOP")) {
                            System.out.println("APOP");
                        } else if (input.startsWith("STAT")) {
                            Integer sum = 0, nb = 0;
                            assert user != null;
                            for (Mail mail : user.getMails()) {
                                sum += mail.getSize();
                                nb++;
                            }
                            output = new StringBuilder(" +OK " + nb + " " + sum);
                        } else if (input.startsWith("RETR")) {
                            Integer id = Integer.parseInt(input.split(" ")[1]);
                            assert user != null;
                            for (Mail mail : user.getMails()) {
                                if (mail.getMessageId().equals(id)) {
                                    output = new StringBuilder("+OK " + mail.getMessageId() + " " + mail.getSize() + "\n");
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
                            output = new StringBuilder("+OK " + user.getMails().size() + " messages:");
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
