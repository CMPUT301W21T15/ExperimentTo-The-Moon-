package com.example.Experiment_To_The_Moon;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.io.Serializable;
import java.security.acl.Owner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class to represent and create each trial of an experiment.
 */

public class Trial implements Serializable {
    // the Trial class represents a single trial in an experiment
    // The name of the experiment that this trial belongs to
    private String Name;
    //The Date the Trial was created on
    private String createdOn;
    //The type of experiment that this Trial is for
    private String Type;
    // the ID of the creator of the trial
    private String  created_by;
    //the location that the trial has taken place
    private ArrayList<Double> location = new ArrayList<>();
    //only set if not given a correct type string and used to show that trial contains no valid data
    private Boolean corrupted;
    // uesed to hold data if type is a measurement
    private double Measurement;
    // used to hold data if the type is a non negative integer or a count
    private int Counting;
    private Boolean outcome;  // true represents success and false represents failure
    private int tempInt=0;
    // cannot modify individual trials

    /**
     *
     * @param outcome
     * Outcome is a String that contains the data for the experiment. If it is a pass it contains the string True if it is a fail it contains the string "Fail"
     * if it is a numerical experiment then it contains the number as a String.
     * @param Owner
     * the ID of the creator of the trial
     * @param type
     * The type of experiment that this Trial is for
     * @param ExpName
     * experiment name
     */
    //Sources Double parse was found on https://www.geeksforgeeks.org/convert-string-to-double-in-java/ written by https://auth.geeksforgeeks.org/user/Rajput-Ji
    // parseInt was found on https://www.javatpoint.com/java-string-to-int
    public Trial(String outcome, String Owner, String type, String ExpName) {
        corrupted=false;
        this.Name=ExpName;
        switch (type) {
            case "Measurement":
                Measurement = Double.parseDouble(outcome);
                break;
            case "NonNegInt":
                Counting = Integer.parseInt(outcome);
                break;
            case "Binomial":
                if (outcome.equals("Pass")) {
                    this.outcome = true;
                }
                if (outcome.equals("Fail")) {
                    this.outcome = false;
                }
                break;
            case "Count":
                Counting = Integer.parseInt(outcome);
                if (Counting < 0) {
                    Counting = 0;
                }
                break;
            default:
                corrupted = true;
                break;
        }

        this.createdOn = setDateInternal();
        created_by=Owner;
        this.Type=type;
        Name=ExpName;
        location.add(0.00); // latitude
        location.add(0.00); // longitude
    }

    // not for out of class use
    private String setDateInternal() {
        Date tempDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(tempDate);
    }

    /**
     *
     * @param new_date
     * String representing date
     */
    public void setDate(String new_date) { this.createdOn = new_date; }

    /**
     *
     * @return
     * String representing date
     */
    public String getDate() { return this.createdOn; }


    /**
     * @deprecated
     */
    public Boolean getOutcome() {
        return outcome;
    }  // serves no practical purpose but makes program extendable

    /**
     *
     * @return
     * The name of the experiment that this trial belongs to
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param trial_type
     * String representing trial type
     */
    public void setName(String trial_type) {
        this.Name = trial_type;
    }

    /**
     *
     * @return
     * the ID of the creator of the trial
     */
    public String getCreated_by() {
        return created_by;
    }

    /**
     *
     * @param created_by
     * the ID of the creator of the trial
     */
    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    /**
     *
     * @return
     * ArrayList of doubles containing latitude and longitude
     */
    public ArrayList<Double> getLocation() {
        return location;
    }

    /**
     *
     * @param l1
     * latitude
     * @param l2
     * longitude
     */
    public void setLocation(double l1, double l2) {
        this.location.set(0, l1);
        this.location.set(1, l2);
    }

    /**
     *
     * @return
     * trial type
     */
    public String getType(){
        return Type;
    }

    /**
     *
     * @return
     * data for measurement type trials
     */
    public double getMeasurementData(){
        return Measurement;
    }

    /**
     *
     * @return
     * data for count type trials
     */
    public int getCountData(){
        return Counting;
    }

    /**
     *
     * @return
     * data for non-negativer integer type trials
     */
    public int getNonNegIntData(){
        return Counting;
    }

    /**
     *
     * @return
     * data for binomial type trials
     */
    public Boolean getBinomialData(){
        return outcome;
    }

    /**
     * Takes in an int that is the number of trials an experiment has then puts the trial into the database
     * @param total
     * number of trials in an experiment
     */
    public void updateDatabase(int total){
        String tempString="Experiments/";
        tempString=tempString+Name;
        String tempString2="/Trials/";
        tempString=tempString+tempString2;
        CollectionReference dataBase= FirebaseFirestore.getInstance().collection(tempString);
        Map<String, Object> data = new HashMap<>();
        data.put("trialType", Type);
        data.put("createdBy",created_by);
        data.put("latitude", getLocation().get(0));
        data.put("longitude", getLocation().get(1));
        data.put("location", getLocation());
        data.put("date", getDate());
        if(Type.equals("Measurement")){
            data.put("data", Measurement);
        }
        if(Type.equals("NonNegInt")){
            data.put("data",Counting);
        }
        if(Type.equals("Count")){
            data.put("data",Counting);
        }
        if(Type.equals("Binomial")){
            data.put("data",outcome);
        }
        tempString=Integer.toString(total);
        dataBase
                .document(tempString)
                .set(data);

    }

    /**
     * Takes in a String that is a user ID and checks if they are in the blacklist for that experiment
     * If they are in the Blacklist returns True otherwise returns false
     * @param id
     * user ID
     * @return
     * if that user is in blacklist
     */
    public Boolean checkBan(String id){
        boolean inList=false;
        String tempString="Experiments/";
        tempString=tempString+Name;
        String tempString2="/Blacklist/";
        tempString=tempString+tempString2;
        CollectionReference dataBase= FirebaseFirestore.getInstance().collection(tempString);
        dataBase.addSnapshotListener((queryDocumentSnapshots, e) -> {
            // clear the old list
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String temp = doc.getId();
                if(temp.equals(id)){tempInt=1;}
            }
        });
        if(tempInt==1){
            inList=true;
            tempInt=0;

        }
        return inList;
    }

}