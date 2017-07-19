package com.linked_sys.hns.Model;

public class Teacher {
    private int teacherID;
    private String teacherName;

    public Teacher(int teacherID, String teacherName) {
        this.teacherID = teacherID;
        this.teacherName = teacherName;
    }

    public int getTeacherID() {
        return teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return teacherName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Teacher) {
            Teacher t = (Teacher) obj;
            if (t.getTeacherName().equals(teacherName) && t.getTeacherID() == teacherID) return true;
        }

        return false;
    }
}
