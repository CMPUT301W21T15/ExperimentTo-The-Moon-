package com.example.mholmstr_trialbook;

import java.io.Serializable;
import java.util.ArrayList;

public class Experiment implements Serializable {
    // the Experiment class represents an experiment within the program's collection of experiments
    private long date;
    private String description;
    private final ArrayList<Trial> successes = new ArrayList<>();
    private final ArrayList<Trial> failures = new ArrayList<>();

    public Experiment(long date, String description) {
        setDate(date);
        setDescription(description);
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description + "\n";
    }

    public int getSuccesses() {return successes.size(); }

    public void addSuccess() {successes.add(new Trial(true));}

    public int getFailures() {return failures.size(); }

    public void addFailure() {failures.add(new Trial(false));}

    public String getSummary() {
        return "Total trials: "+(getSuccesses()+getFailures()) +
                "\nSuccess rate: " + (getSuccesses()/(float)(getSuccesses()+getFailures()));
    }

}
