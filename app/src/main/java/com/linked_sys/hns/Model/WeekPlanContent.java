package com.linked_sys.hns.Model;

public class WeekPlanContent {
    public int ID;
    public String Subject;
    public String HomeWork;
    public String StudentHomeWork;
    public String ParentNote;
    public String TeacherNote;

    public WeekPlanContent(int ID, String subject, String homeWork, String studentHomeWork, String parentNote, String teacherNote) {
        this.ID = ID;
        Subject = subject;
        HomeWork = homeWork;
        StudentHomeWork = studentHomeWork;
        ParentNote = parentNote;
        TeacherNote = teacherNote;
    }

    public int getID() {
        return ID;
    }

    public String getSubject() {
        return Subject;
    }

    public String getHomeWork() {
        return HomeWork;
    }

    public String isStudentHomeWork() {
        return StudentHomeWork;
    }

    public String getParentNote() {
        return ParentNote;
    }

    public String getTeacherNote() {
        return TeacherNote;
    }
}
