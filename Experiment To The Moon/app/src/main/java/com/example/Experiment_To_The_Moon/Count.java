package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

public class Count extends Experiment implements Serializable {

    private int total;
    private boolean geo_location;  // maybe

    public Count(String name, String description, String region, String min_trials, boolean geo_location ) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.minTrials = min_trials;
        this.isEnd = false;
        this.isPublished = false;
        this.type = "Count";
    }
}
