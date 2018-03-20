package com.tem.server;

import java.util.ArrayList;
import java.util.Date;

public class User {

    static private ArrayList<User> users = new ArrayList<>();

    private String username;
    private String password;
    private String address;
    private Boolean lock = false;

    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }

    private ArrayList<Mail> mails = new ArrayList<>();

    public ArrayList<Mail> getMails() {
        return mails;
    }

    public void addMail(Mail m){
        mails.add(m);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }


    public static User getUser(String username){
        for(User user: users){
            if(user.username.equals(username)) {
                sendMessages(user);
                return user;
            }
        }
        return null;
    }

    private static void sendMessages(User user) {
        Mail mail1 = new Mail(user);
        mail1.setContent("This is a first message to say hello.");
        mail1.setDate(new Date());
        mail1.setFromAdress("@gmail.com");
        mail1.setFromName("Artem");
        mail1.setMessageId(1);
        mail1.setObject("The first test message");

        Mail mail2 = new Mail(user);
        mail2.setContent("This is a second message to say hello. I made it for changed zie of message.");
        mail2.setDate(new Date());
        mail2.setFromAdress("@gmail.com");
        mail2.setFromName("Kate");
        mail2.setMessageId(2);
        mail2.setObject("The second test message");
    }

    User(String username, String password){
        this.username = username;
        this.password = password;
        this.address = username + "@gmail.com";
        users.add(this);
    }
}
