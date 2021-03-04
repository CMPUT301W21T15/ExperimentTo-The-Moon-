package com.example.malmond_trialbook;

import java.io.Serializable;
import java.util.ArrayList;

public class Experiment implements Serializable {
    private String date;
    private String description;
    private ArrayList<String> trials = new ArrayList<>();

    public Experiment(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public String getSuccessRate() {
        float rate;
        int successes = 0;
        int fails = 0;
        for (int i = 0; i < trials.size(); i++) {
            if (trials.get(i) == "Success") {
                successes++;
            } else {
                fails++;
            }
        }

        if ((fails == 0 ) && (successes == 0)) {     // to stop division by zero
            rate = 100.0f;
        } else {
            rate = (float) successes / (successes + fails);
        }

        return "Success rate is " + rate;
    }

    public void addSuccess() {
        trials.add("Success");
        return;
    }

    public void addFail() {
        trials.add("Failure");
        return;
    }

    public ArrayList<String> getTrials() {
        return trials;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
