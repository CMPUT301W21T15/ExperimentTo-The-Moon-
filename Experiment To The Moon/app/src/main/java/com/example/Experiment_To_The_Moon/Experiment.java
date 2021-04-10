package com.example.Experiment_To_The_Moon;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Experiment implements Serializable {
    // the Experiment class represents an experiment within the program's collection of experiments
    public String owner;
    public String name;
    public String description;
    public String region;
    public Boolean isEnd;
    public Boolean isPublished;
    private ArrayList<Integer> blacklist = new ArrayList<Integer>();
    private ArrayList<Trial> results = new ArrayList<Trial>();
    public String minTrials;
    public String type;
    private ArrayList<double[]> acceptedLocations = new ArrayList<double[]>();
    public boolean needALocation;

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

    public void showMap() {
        // placeholder method
    }

    public void clearResults() {
        this.results.clear();
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        if (description.endsWith("\n")) this.description = description;
        else this.description = description + "\n";
        // formatting to make experiment list display nicely
    }

    public String getName() { return this.name; }

    public String getRegion() { return this.region; }

    public String getMinTrials() { return this.minTrials; }

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
        return this.results.size();
    }

    public String getType() { return this.type; }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean checkLocation(double[] location){
        for(int i=results.size();i<0;i--){
            double[] temp= acceptedLocations.get(i);
            if(temp[0] == location[0]){
                if (temp[1]==location[1])return true;
            }
        }
        return false;
    };
    public boolean needLocation(){
      return needALocation;
    };
}
