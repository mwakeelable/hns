package com.linked_sys.hns.Model;

public class MailSender {
    private int mailID;
    private String firstName;
    private String lastName;
    private String subject;
    private String date;
    private boolean read;

    public MailSender(int mailID, String firstName, String lastName, String subject, String date, boolean read) {
        this.mailID = mailID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subject = subject;
        this.date = date;
        this.read = read;
    }

    public int getMailID() {
        return mailID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public boolean isRead() {
        return read;
    }
}