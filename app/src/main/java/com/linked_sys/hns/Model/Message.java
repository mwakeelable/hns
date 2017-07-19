package com.linked_sys.hns.Model;

public class Message {
    private int id;
    private String subject;
    private String message;
    private String timestamp;
    private String picture;
    private boolean isRead;
    private int color = -1;
    private String senderName1;
    private String senderName4;
    private int senderID;

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getColor() {
        return color;
    }

    public String getSenderName1() {
        return senderName1;
    }

    public void setSenderName1(String senderName1) {
        this.senderName1 = senderName1;
    }

    public String getSenderName4() {
        return senderName4;
    }

    public void setSenderName4(String senderName4) {
        this.senderName4 = senderName4;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
