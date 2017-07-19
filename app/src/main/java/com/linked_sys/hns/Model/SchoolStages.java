package com.linked_sys.hns.Model;

/**
 * Created by wakeel on 11/01/17.
 */

public class SchoolStages {
    private int stageID;
    private String stageName;

    public int getStageID() {
        return stageID;
    }

    public String getStageName() {
        return stageName;
    }

    public SchoolStages(int stageID, String stageName) {

        this.stageID = stageID;
        this.stageName = stageName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return stageName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SchoolStages) {
            SchoolStages s = (SchoolStages) obj;
            if (s.getStageName().equals(stageName) && s.getStageID() == stageID) return true;
        }

        return false;
    }
}
