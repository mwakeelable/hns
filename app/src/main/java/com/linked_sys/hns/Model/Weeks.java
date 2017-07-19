package com.linked_sys.hns.Model;

public class Weeks {
    public int weekID;
    public String weekName;

    public Weeks(int weekID, String weekName) {
        this.weekID = weekID;
        this.weekName = weekName;
    }

    public int getWeekID() {
        return weekID;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekID(int weekID) {
        this.weekID = weekID;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return weekName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Weeks) {
            Weeks w = (Weeks) obj;
            if (w.getWeekName().equals(weekName) && w.getWeekID() == weekID) return true;
        }

        return false;
    }
}
