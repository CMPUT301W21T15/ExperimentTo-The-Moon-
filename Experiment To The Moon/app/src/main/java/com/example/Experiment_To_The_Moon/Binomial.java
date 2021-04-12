package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

/**
 * This class represents a binomial experiment
 */
public class Binomial extends Experiment implements Serializable {

    private float percentage;
    private int pass;
    private int fail;

    /**
     *
     * @param name
     * experiment name
     * @param owner
     * owner UID
     * @param description
     * experiment description
     * @param end
     * whether the experiment has ended or not (String)
     * @param region
     * experiment regions
     * @param min_trials
     * minimum number of trials to end the experiment
     * @param geo_location
     * whether geolocation is required for the experiment or not (boolean)
     */
    public Binomial(String name, String owner, String description, String end, String region, String min_trials, boolean geo_location ) {
        this.name = name;
        this.owner = owner;
        this.setDescription(description);
        this.region = region;
        this.minTrials = min_trials;
        if (end.equals("true")) this.isEnd = true;
        if (end.equals("false")) this.isEnd = false;
        this.isPublished = false;
        this.type = "Binomial";
        this.needALocation=geo_location;
    }
}
