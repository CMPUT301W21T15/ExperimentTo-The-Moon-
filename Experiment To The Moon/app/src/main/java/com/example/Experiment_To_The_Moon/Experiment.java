package com.example.Experiment_To_The_Moon;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is an abstract base for experiment subclasses
 */
public abstract class Experiment implements Serializable {
    // the Experiment class represents an experiment within the program's collection of experiments
    public String owner;
    public String name;
    public String description;
    public String region;
    public Boolean isEnd;
    public Boolean isPublished;
    private final ArrayList<Integer> blacklist = new ArrayList<>();
    private final ArrayList<Trial> results = new ArrayList<>();
    public String minTrials;
    public String type;
    private final ArrayList<Double> acceptedLocations = new ArrayList<>();
    public boolean needALocation;

    /**
     * publishes the experiment if unpublished, unpublishes it if published
     */
    public void togglePublish() {
        isPublished = !isPublished;
    }

    /**
     *
     * @return
     * whether the experiment is published or not
     */
    public boolean getIsPublished() { return this.isPublished; }

    /**
     * ends the experiment if not ended, unends the experiment if ended
     */
    public void toggleEnd() {
        isEnd = !isEnd;
    }

    /**
     *
     * @return
     * whether the experiment has ended or not
     */
    public boolean getIsEnd() { return this.isEnd; }

    /**
     * @deprecated
     */
    public void blacklistUser(int user) {
        blacklist.add(user);
    }

    /**
     * adds a trial to the experiment
     * @param result
     * trial to add
     */
    public void addResult(Trial result) {
        results.add(result);
    }

    /**
     * @deprecated
     */
    public void showMap() {
        // placeholder method
    }

    /**
     * clears trials
     */
    public void clearResults() {
        this.results.clear();
    }

    /**
     *
     * @return
     * experiment description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * special formatting for description
     * @param description
     * experiment description
     */
    public void setDescription(String description) {
        if (description.endsWith("\n")) this.description = description;
        else this.description = description + "\n";
        // formatting to make experiment list display nicely
    }

    /**
     *
     * @return
     * experiment name
     */
    public String getName() { return this.name; }

    /**
     *
     * @return
     * experiment region
     */
    public String getRegion() { return this.region; }

    /**
     *
     * @return
     * minimum number of trials to end the experiment
     */
    public String getMinTrials() { return this.minTrials; }

    /**
     * @deprecated
     */
    public long getDate(){
        return 0;
    }

    /**
     * @deprecated
     */
    public int getSuccesses(){
        return 0;
    }

    /**
     * @deprecated
     */
    public int getFailures(){
        return 0;
    }

    /**
     * @deprecated
     */
    public String getSummary(){
        return "";
    }

    /**
     * @deprecated
     */
    public void setDate(long v){

    }

    /**
     * @deprecated
     */
    public void addSuccess(){

    }

    /**
     * @deprecated
     */
    public void addFailure(){

    }

    /**
     *
     * @return
     * number of trials assigned to the experiment
     */
    public int getTrials(){
        return this.results.size();
    }

    /**
     *
     * @return
     * experiment type
     */
    public String getType() { return this.type; }

    /**
     *
     * @return
     * experiment owner UID
     */
    public String getOwner() {
        return owner;
    }

    /**
     *
     * @param owner
     * experiment owner UID
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * getter method
     * @return
     * whether the experiment needs a location or not (boolean)
     */
    public boolean needLocation(){
      return needALocation;
    }
}
