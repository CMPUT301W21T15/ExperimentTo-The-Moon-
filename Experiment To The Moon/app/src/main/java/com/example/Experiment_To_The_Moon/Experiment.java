package com.example.Experiment_To_The_Moon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public abstract class Experiment implements Serializable {
    // the Experiment class represents an experiment within the program's collection of experiments

    private int owner;
    private String description;
    private String region;
    private Boolean isEnd;
    private Boolean isPublished;
    private ArrayList<Integer> blacklist = new ArrayList<Integer>();
    private ArrayList<Trial> results = new ArrayList<Trial>();
    private int minTrials;

    public void togglePublish() {
        isPublished = !isPublished;
    }

    public void toggleEnd() {
        isEnd = !isEnd;
    }

    public void blacklistUser(int user) {
        blacklist.add(user);
    }

    public void addResult(Trial result) {
        results.add(result);
    }

    public void showStatistics() {
        // placeholder method
    }

    public void showMap() {
        // placeholder method
    }

    public void showForum() {
        // placeholder method
    }

}
