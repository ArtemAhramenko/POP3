package server;

import java.util.ArrayList;
import java.util.Date;

class User {

    static private ArrayList<User> users = new ArrayList<>();

    private ArrayList<Mail> mails = new ArrayList<>();
    private String username;
    private String password;
    private String address;
    private Boolean lock = false;

    Boolean getLock() {
        return lock;
    }

    void setLock(Boolean lock) {
        this.lock = lock;
    }

    ArrayList<Mail> getMails() {
        return mails;
    }

    void addMail(Mail m){
        mails.add(m);
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getAddress() {
        return address;
    }


    static User getUser(String username){
        for(User user: users){
            if(user.username.equals(username)) {
                sendMessages(user);
                return user;
            }
        }
        return null;
    }

    private static void sendMessages(User user) {
        Mail mail1 = createMessage(user);
        mail1.setContent("This is a first message to say hello.");
        mail1.setFromName("Artem");
        mail1.setMessageId(1);

        Mail mail2 = createMessage(user);
        mail2.setContent("This is a second message to say hello. I made it for changed size of message.");
        mail2.setFromName("Kate");
        mail2.setMessageId(2);
    }

    private static Mail createMessage(User user) {
        Mail mail = new Mail(user);
        mail.setDate(new Date());
        mail.setFromAdress("@gmail.com");
        mail.setObject("The test message");
        return mail;
    }

    User(String username, String password){
        this.username = username;
        this.password = password;
        this.address = username + "@gmail.com";
        users.add(this);
    }
}
