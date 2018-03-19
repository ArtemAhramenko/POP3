package com.tem.client;

import javax.swing.*;
import java.awt.*;
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
        createFrame();

        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            Scanner inData = new Scanner(socket.getInputStream());
            PrintWriter outData = new PrintWriter(socket.getOutputStream());
            threadInMessages(inData);
            outMessage(outData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outMessage(PrintWriter outData) {
        Scanner inputMessage = new Scanner(new InputStreamReader(System.in));
        System.out.println("Typing something..");
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

    private void createFrame() {
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
    }

    public static void main(String [] args) {
        new StartClient();
    }

}































