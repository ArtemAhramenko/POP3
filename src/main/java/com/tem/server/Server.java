package com.tem.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Server implements Runnable {

    private Socket socket;
    private PrintWriter outData;
    private Scanner inData;
    private State state;

    enum State{
        Authorisation,
        Password,
        Transaction
    }

    Server(Socket socket, int timeout) throws SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(timeout * 1000);
        state = State.Authorisation;
        System.out.println("[" + socket.getInetAddress() + "] " + "connected" );
    }

    @Override
    public void run() {
        try {
            outData = new PrintWriter(socket.getOutputStream(), true);
            inData = new Scanner(socket.getInputStream());
            StringBuilder output = new StringBuilder("-ERR unknown error");
            User user = null;
            outData.println("+OK POP3 server ready");
            while (inData.hasNext()) {
                String inputData = inData.nextLine();
                System.out.println(inputData);
                switch (state) {
                    case Authorisation:
                        System.out.println("user :" + inputData);
                        if (inputData.startsWith("USER")) {
                            String username = inputData.split(" ")[1];
                            user = User.getUser(username);
                            if (user == null || user.getLock()) {
                                output = new StringBuilder("-ERR never heard of mailbox name");
                            } else {
                                output = new StringBuilder("+OK name is a valid mailbox");
                                state = State.Password;
                            }
                        }
                        break;
                    case Password:
                        if (inputData.startsWith("PASS")) {
                            if (user == null) {
                                System.err.println("ERR unable to lock maildrop");
                            }
                            String pwd = inputData.split(" ")[1];
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
                        if (inputData.startsWith("QUIT")) {
                            user.setLock(false);
                            output = new StringBuilder("+OK");
                            state = State.Authorisation;
                        } else if (inputData.startsWith("NOOP")) {
                            output = new StringBuilder("+OK");
                        } else if (inputData.startsWith("STAT")) {
                            Integer sum = 0, countMessages = 0;
                            for (Mail mail : user.getMails()) {
                                sum += mail.getSize();
                                countMessages++;
                            }
                            output = new StringBuilder(" +OK " + countMessages + " " + sum);
                        } else if (inputData.startsWith("RETR")) {
                            Integer id = Integer.parseInt(inputData.split(" ")[1]);
                            for (Mail mail : user.getMails()) {
                                if (mail.getMessageId().equals(id)) {
                                    output = new StringBuilder("+OK message follows\n" + mail.getMessageId() + " " + mail.getSize() + "\n");
                                    output.append("From: ").append(mail.getFromName()).append(" <").append(mail.getFromAdress()).append(">\n");
                                    output.append("To: ").append(mail.getUser().getUsername()).append(" <").append(mail.getUser().getAddress()).append(">\n");
                                    output.append("Subject: ").append(mail.getObject()).append("\n");
                                    output.append("Date: ").append(mail.getDate().toString()).append("\n");
                                    output.append("Id: ").append(mail.getMessageId()).append("\n");
                                    output.append(mail.getContent());
                                    break;
                                }
                            }
                        } else if (inputData.startsWith("LIST")) {
                            assert user != null;
                            output = new StringBuilder("+OK scan listing follows\n" + user.getMails().size());
                            for (Mail mail : user.getMails()) {
                                output.append("\n").append(mail.getMessageId()).append(" ").append(mail.getSize());
                            }
                        } else {
                            output = new StringBuilder("Unknown command");
                        }
                        break;
                }
                outData.println(output);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[" + socket.getInetAddress() + "] " + "Socket Timeout");
        } catch (IOException e) {
            System.err.println("Stream Error");
        } finally {
            try {
                outData.close();
                inData.close();
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("[" + socket.getInetAddress() + "] " + "disconnected");
        }
    }
}
