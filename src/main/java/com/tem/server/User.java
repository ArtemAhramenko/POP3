package com.tem.server;

import java.util.ArrayList;

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


    static public User getUser(String username){
        for(User user: m_users){
            if(user.username.equals(username))
                return user;
        }
        return null;
    }

    User(String name, String pwd){
        this.username = name;
        this.password = pwd;
        this.address = name + "@gmail.com";
        m_users.add(this);
    }
}
