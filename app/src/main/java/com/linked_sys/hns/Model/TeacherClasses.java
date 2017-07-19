package com.linked_sys.hns.Model;

public class TeacherClasses {
    private int classID;
    private String className;

    public TeacherClasses(int classID, String className) {
        this.classID = classID;
        this.className = className;
    }

    public int getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return className;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TeacherClasses) {
            TeacherClasses c = (TeacherClasses) obj;
            if (c.getClassName().equals(className) && c.getClassID() == classID) return true;
        }

        return false;
    }
}
