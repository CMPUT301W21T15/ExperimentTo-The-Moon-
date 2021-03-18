package com.example.Experiment_To_The_Moon;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Experiment implements Serializable {
    // the Experiment class represents an experiment within the program's collection of experiments

    private int owner;
    public String name;
    public String description;
    public String region;
    public Boolean isEnd;
    public Boolean isPublished;
    private ArrayList<Integer> blacklist = new ArrayList<Integer>();
    private ArrayList<Trial> results = new ArrayList<Trial>();
    public String minTrials;

    public void togglePublish() {
        isPublished = !isPublished;
    }
    public boolean getIsPublished() { return this.isPublished; }

    public void toggleEnd() {
        isEnd = !isEnd;
    }
    public boolean getIsEnd() { return this.isEnd; }

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

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description + "\n";
    }

    public String getName() { return this.name; }

    public String getRegion() { return this.region; }

    public String getMinTrials() { return this.minTrials; }

    /** Made some changes to make sure it runs*/
    public long getDate(){
        return 0;
    }

    public int getSuccesses(){
        return 0;
    }
    public int getFailures(){
        return 0;
    }
    public String getSummary(){
        return "";
    }
    public void setDate(long v){

    }
    public void addSuccess(){

    }
    public void addFailure(){

    }
    public int getTrials(){
        return results.size();
    }



}
