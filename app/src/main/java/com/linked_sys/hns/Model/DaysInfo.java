package com.linked_sys.hns.Model;


public class DaysInfo {
    private String dayName;
    private int dayID;

    public DaysInfo(String dayName, int dayID) {
        this.dayName = dayName;
        this.dayID = dayID;
    }

    public void setDayName(String dayName){
        this.dayName = dayName;
    }

    public String getDayName(){
        return this.dayName;
    }

    public void setDayID(int dayID){
        this.dayID = dayID;
    }

    public int getDayID(){
        return this.dayID;
    }
}
