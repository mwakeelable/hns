package com.linked_sys.hns.Model;

public class Tasks {
    private String subject, homework, status;
    private int id;
    private int color;

    public Tasks() {

    }

    public String getSubject() {
        return subject;
    }

    public String getHomework() {
        return homework;
    }

    public String getStatus() {
        return status;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
