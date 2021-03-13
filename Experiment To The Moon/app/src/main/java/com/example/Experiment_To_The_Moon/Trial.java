package com.example.Experiment_To_The_Moon;

import java.io.Serializable;
import java.util.Date;


// the Trial class represents a single trial in an experiment
public class Trial implements Serializable {
    private String trial_type;  // The type of experiment that the trial is for
    private int created_by;  // the ID of the creator of the trial

    private String location;  // the location that the trial has taken place
    private final Boolean outcome;  // true represents success and false represents failure
    private Date date;

    // cannot modify individual trials

    public Trial(Boolean outcome, long input_date) {
        this.outcome = outcome;
        this.date = new Date(input_date);

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

    public long getDate() { return this.date.getTime(); }

    public void setDate(long new_date) { this.date = new Date(new_date); }

}
