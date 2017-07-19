package com.linked_sys.hns.Model;


public class Lessons {
    private int lessonID;
    private int courseID;
    private String courseName;
    private String lessonName;
    private String videoURL;

    public Lessons() {
    }

    public Lessons(int lessonID, int courseID, String courseName, String lessonName, String videoURL) {
        this.lessonID = lessonID;
        this.courseID = courseID;
        this.courseName = courseName;
        this.lessonName = lessonName;
        this.videoURL = videoURL;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }
}
