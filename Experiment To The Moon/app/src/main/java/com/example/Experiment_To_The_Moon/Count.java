package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

/**
 * This class represents a count experiment
 */
public class Count extends Experiment implements Serializable {

    private int total;
    private boolean geo_location;

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
     * @param published
     * whether the experiment has been published or not (String)
     */
    public Count(String name, String owner, String description, String end, String region, String min_trials, boolean geo_location, String published) {
        this.name = name;
        this.owner = owner;
        this.setDescription(description);
        this.region = region;
        this.minTrials = min_trials;
        if (end.equals("true")) this.isEnd = true;
        if (end.equals("false")) this.isEnd = false;
        if (published.equals("true")) this.isPublished = true;
        if (published.equals("false")) this.isPublished = false;
        this.type = "Count";
        this.needALocation=geo_location;
    }
}
