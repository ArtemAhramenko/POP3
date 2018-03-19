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
            createFrame(new PrintWriter(socket.getOutputStream()));
            threadInMessages(new Scanner(socket.getInputStream()));
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
        JButton button = new JButton("Send");
        JPanel panel = new JPanel();

        frameSettings(frame, panel, button);
        sendButton(button, outData);
    }

    private JFrame frameSettings(JFrame frame, JPanel panel, JButton button) {
        frame.setSize(new Dimension(500, 400));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(addElmsForPanel(panel, button));
        frame.setVisible(true);
        return frame;
    }

    private JPanel addElmsForPanel(JPanel panel, JButton button) {
        textField.setFont(new Font("Courier New", Font.ITALIC, 17));
        textArea.setFont(new Font("Courier New", Font.ITALIC, 17));
        textArea.setEditable(false);
        panel.setLayout(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(textField, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);
        return panel;
    }

    private void sendButton(JButton button, PrintWriter outData) {
        button.addActionListener(e -> {
            Scanner inputMessage = new Scanner(textField.getText());
            outData.println(inputMessage.nextLine());
            outData.flush();
            textField.setText("");
            textField.requestFocus();
        });
    }

    public static void main(String [] args) {
        new StartClient();
    }
}































