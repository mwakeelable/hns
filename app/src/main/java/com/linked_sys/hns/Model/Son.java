package com.linked_sys.hns.Model;

public class Son {
    public int ID;
    public String Name1;
    public String Name4;
    public int classID;
    public String className;
    public int stageID;
    public String stageName;
    public String imgURL;
    public int termID;
    public int schoolID;
    public int tasksCount;

    public Son(int ID, String name1, String name4, int classID, String className, int stageID,
               String stageName, String imgURL, int termID, int schoolID, int tasksCount) {
        this.ID = ID;
        Name1 = name1;
        Name4 = name4;
        this.classID = classID;
        this.className = className;
        this.stageID = stageID;
        this.stageName = stageName;
        this.imgURL = imgURL;
        this.termID = termID;
        this.schoolID = schoolID;
        this.tasksCount = tasksCount;
    }
}
