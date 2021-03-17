package com.example.Experiment_To_The_Moon;

public class NonNegInt extends Experiment {

    private int total;

    public NonNegInt(String name, String description, String region, String min_trials, boolean geo_location ) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.minTrials = min_trials;
        this.isEnd = false;
        this.isPublished = false;
        this.type = "NonNegInt";
    }

}