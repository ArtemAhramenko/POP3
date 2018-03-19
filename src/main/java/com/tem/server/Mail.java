package com.tem.server;

import java.util.Date;

public class Mail {

    private String content;
    private String fromName;
    private String fromAdress;
    private String object;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    private User   user;
    private Date date;
    private Integer messageId;
    private Integer size = 0;

    public Integer getSize() {
        return size;
    }

    Mail(User u){
        u.addMail(this);
        setUser(u);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        size += content.length();
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
        size += fromName.length();
    }

    public String getFromAdress() {
        return fromAdress;
    }

    public void setFromAdress(String fromAdress) {
        this.fromAdress = fromAdress;
        size += fromAdress.length();
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
        size += user.getUsername().length();
        size += user.getAddress().length();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        size += date.toString().length();
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
        size += messageId.toString().length();
    }
}
