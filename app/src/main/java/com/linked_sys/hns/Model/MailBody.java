package com.linked_sys.hns.Model;

public class MailBody {
    private int mailID;
    private int SenderID;
    private String senderName1;
    private String senderName2;
    private String senderName3;
    private String senderName4;
    private String SenderImage;
    private String subject;
    private String sentDate;
    private boolean isRead;
    private String body;
    private int position;
    private int color;

    public MailBody(int mailID,int senderID, String senderName1, String senderName2,
                    String senderName3, String senderName4, String senderImage,
                    String subject, String sentDate, boolean isRead, String body,
                    int position, int color) {
        this.mailID = mailID;
        SenderID = senderID;
        this.senderName1 = senderName1;
        this.senderName2 = senderName2;
        this.senderName3 = senderName3;
        this.senderName4 = senderName4;
        SenderImage = senderImage;
        this.subject = subject;
        this.sentDate = sentDate;
        this.isRead = isRead;
        this.body = body;
        this.position = position;
        this.color = color;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getMailID() {
        return mailID;
    }

    public void setMailID(int mailID) {
        this.mailID = mailID;
    }

    public int getSenderID() {
        return SenderID;
    }

    public void setSenderID(int senderID) {
        SenderID = senderID;
    }

    public String getSenderName1() {
        return senderName1;
    }

    public void setSenderName1(String senderName1) {
        this.senderName1 = senderName1;
    }

    public String getSenderName2() {
        return senderName2;
    }

    public void setSenderName2(String senderName2) {
        this.senderName2 = senderName2;
    }

    public String getSenderName3() {
        return senderName3;
    }

    public void setSenderName3(String senderName3) {
        this.senderName3 = senderName3;
    }

    public String getSenderName4() {
        return senderName4;
    }

    public void setSenderName4(String senderName4) {
        this.senderName4 = senderName4;
    }

    public String getSenderImage() {
        return SenderImage;
    }

    public void setSenderImage(String senderImage) {
        SenderImage = senderImage;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
