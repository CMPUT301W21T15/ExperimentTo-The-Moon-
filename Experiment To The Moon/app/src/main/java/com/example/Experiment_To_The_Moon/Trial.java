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

import java.io.Serializable;
import java.security.acl.Owner;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
// This is a class to represent and create each trial of an experiment.

public class Trial implements Serializable {
    // the Trial class represents a single trial in an experiment
    // The name of the experiment that this trial belongs to
    private String Name;
    //The Date the Trial was created on
    private Date createdOn;
    //The type of experiment that this Trial is for
    private String Type;
    // the ID of the creator of the trial
    private String  created_by;
    //the location that the trial has taken place
    private String location;
    //only set if not given a correct type string and used to show that trial contains no valid data
    private Boolean corrupted;
    // uesed to hold data if type is a measurement
    private double Measurement;
    // used to hold data if the type is a non negative integer or a count
    private int Counting;
    private Boolean outcome;  // true represents success and false represents failure
    private int tempInt=0;
    // cannot modify individual trials

    //Outcome is a String that contains the data for the experiment. If it is a pass it contains the string True if it is a fail it contains the string "Fail"
    // if it is a numerical experiment then it contains the number as a String.
    //Sources Double parse was found on https://www.geeksforgeeks.org/convert-string-to-double-in-java/ written by https://auth.geeksforgeeks.org/user/Rajput-Ji
    // parseInt was found on https://www.javatpoint.com/java-string-to-int
    public Trial(String outcome, String Owner, String type, String ExpName) {
        corrupted=false;
        this.Name=ExpName;
        if(type.equals("Measurement")){
            Measurement=Double.parseDouble(outcome);
        } else if (type.equals("NonNegInt")) {
            Counting=Integer.parseInt(outcome);
        } else if (type.equals("Binomial")) {
            if(outcome.equals("Pass")) {
                this.outcome=true;
            }
            if(outcome.equals("Fail")) {
                this.outcome=false;
            }
        } else if (type.equals("Count")) {
            Counting=Integer.parseInt(outcome);
            if(Counting<0) {Counting=0;}
        } else{corrupted=true;}

        createdOn= new Date();
        created_by=Owner;
        this.Type=type;
        Name=ExpName;
    }

    public Boolean getOutcome() {
        return outcome;
    }  // serves no practical purpose but makes program extendable

    public String getName() {
        return Name;
    }

    public void setName(String trial_type) {
        this.Name = trial_type;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getType(){
        return Type;
    }
    public double getMeasurementData(){
        return Measurement;
    }
    public int getCountData(){
        return Counting;
    }
    public int getNonNegIntData(){
        return Counting;
    }
    public Boolean getBinomialData(){
        return outcome;
    }

    //Takes in an int that is the number of trials an experiment has then puts the trial into the database
    public void updateDatabase(int total){
        String tempString="Experiments/";
        tempString=tempString+Name;
        String tempString2="/Trials/";
        tempString=tempString+tempString2;
        CollectionReference dataBase= FirebaseFirestore.getInstance().collection(tempString);
        Map<String, Object> data = new HashMap< String, Object>();
        data.put("trialType", Type);
        data.put("createdBy",created_by);
        data.put("location","not implemented yet");
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

    //Takes in a String that is a user ID and checks if they are in the blacklist for that experiment
    // Of they are in the Blacklist returns True otherwise returns false

    public Boolean checkBan(String id){
        boolean inList=false;
        String tempString="Experiments/";
        tempString=tempString+Name;
        String tempString2="/Blacklist/";
        tempString=tempString+tempString2;
        CollectionReference dataBase= FirebaseFirestore.getInstance().collection(tempString);
        dataBase.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // clear the old list
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String temp = doc.getId();
                    if(temp.equals(id)){tempInt=1;}
                }
            }

        });
        if(tempInt==1){
            inList=true;
            tempInt=0;

        }
        return inList;
    }

}