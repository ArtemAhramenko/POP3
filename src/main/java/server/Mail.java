package server;

import java.util.Date;

class Mail {

    private User user;
    private Date date;
    private Integer messageId;
    private Integer size = 0;
    private String content;
    private String fromName;
    private String fromAdress;
    private String object;

    Mail(User user){
        user.addMail(this);
        setUser(user);
    }

    Integer getSize() {
        return size;
    }

    String getObject() {
        return object;
    }

    void setObject(String object) {
        this.object = object;
    }

    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
        size += content.length();
    }

    String getFromName() {
        return fromName;
    }

    void setFromName(String fromName) {
        this.fromName = fromName;
        size += fromName.length();
    }

    String getFromAdress() {
        return fromAdress;
    }

    void setFromAdress(String fromAdress) {
        this.fromAdress = fromAdress;
        size += fromAdress.length();
    }

    User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
        size += user.getUsername().length();
        size += user.getAddress().length();
    }

    Date getDate() {
        return date;
    }

    void setDate(Date date) {
        this.date = date;
        size += date.toString().length();
    }

    Integer getMessageId() {
        return messageId;
    }

    void setMessageId(Integer messageId) {
        this.messageId = messageId;
        size += messageId.toString().length();
    }
}
