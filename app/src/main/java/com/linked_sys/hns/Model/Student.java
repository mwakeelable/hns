package com.linked_sys.hns.Model;

/**
 * Created by wakeel on 11/01/17.
 */

public class Student {
    private int studentID;
    private int parentID;
    private String studentName;

    public int getStudentID() {
        return studentID;
    }

    public int getParentID() {
        return parentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public Student(int studentID, int parentID, String studentName) {

        this.studentID = studentID;
        this.parentID = parentID;
        this.studentName = studentName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return getStudentName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Student) {
            Student s = (Student) obj;
            if (s.getStudentName().equals(studentName) && s.getStudentID() == studentID && s.getParentID() == parentID) return true;
        }

        return false;
    }
}
