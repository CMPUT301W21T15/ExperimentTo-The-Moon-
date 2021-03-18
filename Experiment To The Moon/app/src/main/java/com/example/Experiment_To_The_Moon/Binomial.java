package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

public class Binomial extends Experiment implements Serializable {

    private float percentage;
    private int pass;
    private int fail;

    public Binomial(String name, String owner, String description, String region, String min_trials, boolean geo_location ) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.region = region;
        this.minTrials = min_trials;
        this.isEnd = false;
        this.isPublished = false;
        this.type = "Binomial";
    }
}
