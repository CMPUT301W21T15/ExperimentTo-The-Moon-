package com.example.Experiment_To_The_Moon;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String uid;
    private String contactInfo;
    private ArrayList<String> subscriptions = new ArrayList<>(); // This is a list of names of experiments subbed to

    public User(String uid, String contactInfo) {
        this.uid = uid;
        this.contactInfo = contactInfo;
    }

    public String getUid() {
        return uid;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<String> subscriptions) {
        this.subscriptions = (ArrayList<String>) subscriptions.clone();
    }

    public void addSubscription(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.subscriptions.add(name);
        db.collection("Users").document(getUid()).update("subscriptionList", getSubscriptions());
    }

    public void removeSubscription(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.subscriptions.remove(name);
        db.collection("Users").document(getUid()).update("subscriptionList", getSubscriptions());
    }
}
