package com.tem.server;

import java.util.ArrayList;
import java.util.Date;

public class User {

    static private ArrayList<User> m_users = new ArrayList<>();

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
        for(User user: m_users){
            if(user.username.equals(username)) {
                sendMessages(user);
                return user;
            }
        }
        return null;
    }

    private static void sendMessages(User user) {
        Mail mail = new Mail(user);
        mail.setContent("This is a first message to say hello.");
        mail.setDate(new Date());
        mail.setFromAdress("@gmail.com");
        mail.setFromName("Artem");
        mail.setMessageId(1);
        mail.setObject("The first test message");

        Mail mail2 = new Mail(user);
        mail2.setContent("This is a second message to say hello. I made it for changed zie of message.");
        mail2.setDate(new Date());
        mail2.setFromAdress("@gmail.com");
        mail2.setFromName("Artem");
        mail2.setMessageId(2);
        mail2.setObject("The second test message");
    }

    User(String name, String pwd){
        this.username = name;
        this.password = pwd;
        this.address = name + "@gmail.com";
        m_users.add(this);
    }
}
