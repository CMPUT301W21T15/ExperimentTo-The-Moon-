package com.example.Experiment_To_The_Moon;

import java.io.Serializable;
import java.util.ArrayList;


//public abstract class Experiment implements Serializable {
public class Experiment implements Serializable {
    /** Made some changes to make sure it runs*/
    // the Experiment class represents an experiment within the program's collection of experiments

    private int owner;
    private String description;
    private String region;
    private Boolean isEnd;
    private Boolean isPublished;
    private ArrayList<Integer> blacklist = new ArrayList<Integer>();
    private ArrayList<Trial> results = new ArrayList<Trial>();
    private int minTrials;

    /** Made some changes to make sure it runs*/
    public Experiment(){

    }
    public Experiment(long date, String description){

    }
    /** */


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

    /** Made some changes to make sure it runs*/
    public long getDate(){
       return 0;
    }
    public String getDescription(){
        return "";
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
    public void setDescription(String v){

    }
    /** */
}
