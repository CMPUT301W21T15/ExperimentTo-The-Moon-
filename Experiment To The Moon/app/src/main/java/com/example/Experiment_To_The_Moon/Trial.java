package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

public class Trial implements Serializable {
    // the Trial class represents a single trial in an experiment
    private final Boolean outcome;  // true represents success and false represents failure
    // cannot modify individual trials

    public Trial(Boolean outcome) {
        this.outcome = outcome;
    }

    public Boolean getOutcome() {
        return outcome;
    }  // serves no practical purpose but makes program extendable
}
