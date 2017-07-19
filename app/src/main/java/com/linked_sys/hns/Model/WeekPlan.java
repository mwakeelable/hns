package com.linked_sys.hns.Model;


public class WeekPlan {
    public int lectureID;
    public int weekDayID;
    public String courseName;
    public int planID;

    public WeekPlan(int lectureID, int weekDayID, String courseName, int planID) {
        this.lectureID = lectureID;
        this.weekDayID = weekDayID;
        this.courseName = courseName;
        this.planID = planID;
    }
}
