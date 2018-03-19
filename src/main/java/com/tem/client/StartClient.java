package com.tem.client;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class StartClient {

    private JTextArea textArea = new JTextArea();
    private JTextField textField = new JTextField();
    private StartClient() {

        final int SERVER_PORT = 1110;
        final String SERVER_HOST = "localhost";

        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            Scanner inData = new Scanner(socket.getInputStream());
            createFrame(new PrintWriter(socket.getOutputStream()));
            threadInMessages(inData);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void createFrame(PrintWriter outData) {
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
            Scanner inputMessage = new Scanner(textField.getText());
            outData.println(inputMessage.nextLine());
            outData.flush();
        });
    }

    public static void main(String [] args) {
        new StartClient();
    }
}































