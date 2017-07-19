package com.linked_sys.hns.Model;


public class TimeTable {
    public int lectureID;
    public int weekDayID;
    public String courseName;
    public String className;

    public TimeTable(int lectureID, int weekDayID, String courseName, String className) {
        this.lectureID = lectureID;
        this.weekDayID = weekDayID;
        this.courseName = courseName;
        this.className = className;
    }
}
