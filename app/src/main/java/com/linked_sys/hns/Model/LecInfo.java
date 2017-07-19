package com.linked_sys.hns.Model;

public class LecInfo {
    private String lecName;
    private int lecID;

    public void setLecName(String lecName){
        this.lecName = lecName;
    }

    public String getLecName(){
        return this.lecName;
    }

    public void setLecID(int lecID){
        this.lecID = lecID;
    }

    public int getLecID(){
        return this.lecID;
    }

    public LecInfo(String lecName, int lecID) {
        this.lecName = lecName;
        this.lecID = lecID;
    }
}
