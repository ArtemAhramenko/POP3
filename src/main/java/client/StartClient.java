package client;

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
            createFrameComponents(new PrintWriter(socket.getOutputStream()));
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
            catch (Exception ignored) {}
        }).start();
    }

    private void createFrameComponents(PrintWriter outData) {
        JFrame frame = new JFrame("POP3 Client");
        JPanel panel = new JPanel();
        JButton sendButton = new JButton("Send");
        JScrollPane scrollPane = new JScrollPane(textArea);
        componentsSettings(frame, panel, sendButton, scrollPane);
        sendButtonClick(sendButton, outData);
    }

    private void componentsSettings(JFrame frame, JPanel panel, JButton button, JScrollPane scrollPane) {
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textField.setFont(new Font("Courier New", Font.ITALIC, 17));
        textArea.setFont(new Font("Courier New", Font.ITALIC, 17));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        frame.add(scrollPane);
        frame.setSize(new Dimension(750, 650));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(addElmsForPanel(panel, button, scrollPane));
        frame.setVisible(true);
    }

    private JPanel addElmsForPanel(JPanel panel, JButton button, JScrollPane scrollPane) {
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(textField, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);
        return panel;
    }

    private void sendButtonClick(JButton sendButton, PrintWriter outData) {
        sendButton.addActionListener(e -> {
            if (textField.getText().length() != 0){
                Scanner inputMessage = new Scanner(textField.getText());
                textArea.append(textField.getText()+"\n");
                outData.println(inputMessage.nextLine());
                outData.flush();
                textField.setText("");
                textField.requestFocus();
            }
        });
    }

    public static void main(String [] args) {
        new StartClient();
    }
}































