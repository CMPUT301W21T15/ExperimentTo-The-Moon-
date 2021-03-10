package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

public class Trial implements Serializable {
    // the Trial class represents a single trial in an experiment
    // The type of experiment that the trial is for
    private String trial_type;
    // the ID of the creator of the trial
    private int created_by;
    //the location that the trial has taken place
    private String location;
    private final Boolean outcome;  // true represents success and false represents failure
    // cannot modify individual trials

    public Trial(Boolean outcome) {
        this.outcome = outcome;
    }

    public Boolean getOutcome() {
        return outcome;
    }  // serves no practical purpose but makes program extendable

    public String getTrial_type() {
        return trial_type;
    }

    public void setTrial_type(String trial_type) {
        this.trial_type = trial_type;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
