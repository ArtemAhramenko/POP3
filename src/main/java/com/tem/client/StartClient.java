package com.tem.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class StartClient {

    private JTextArea textArea = new JTextArea();
    private JTextField textField = new JTextField();

    private StartClient() {

        int SERVER_PORT = 1110;
        String SERVER_HOST = "localhost";

        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            createFrame(socket);
            Scanner inData = new Scanner(socket.getInputStream());
            //   PrintWriter outData = new PrintWriter(socket.getOutputStream());
            threadInMessages(inData);
            //   outMessage(outData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outMessage(PrintWriter outData) {
        Scanner inputMessage = new Scanner(textField.getText());
        while (true) {
            if (inputMessage.hasNext()) {
                outData.println(inputMessage.nextLine());
                outData.flush();
            }
        }
    }

    private void threadInMessages(Scanner inData) {
        new Thread(() -> {
            try {
                while (true) {
                    if (inData.hasNext()) {
                        textArea.append(inData.nextLine()+"\n");
                    }
                }
            }
            catch (Exception ignored){}
        }).start();
    }

    private void createFrame(Socket socket) {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(500, 400));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JButton button = new JButton("Send");

        panel.add(textArea, BorderLayout.CENTER);
        panel.add(textField, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);


        button.addActionListener(e -> {
            try {
                outMessage(new PrintWriter(socket.getOutputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    public static void main(String [] args) {
        new StartClient();
    }

}































