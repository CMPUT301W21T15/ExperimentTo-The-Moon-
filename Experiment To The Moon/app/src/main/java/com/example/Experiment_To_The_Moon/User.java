package com.example.Experiment_To_The_Moon;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a user
 */
public class User implements Serializable {

    private String uid;
    private String contactInfo;
    private ArrayList<String> subscriptions = new ArrayList<>(); // This is a list of names of experiments subbed to

    /**
     *
     * @param uid
     * unique identifier for users
     * @param contactInfo
     * user contact information
     */
    public User(String uid, String contactInfo) {
        this.uid = uid;
        this.contactInfo = contactInfo;
    }

    /**
     *
     * @return
     * user unique identifier
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     * @return
     * user contact information
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     *
     * @param contactInfo
     * user contact information
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     *
     * @return
     * ArrayList of strings representing experiments the user is subscribed to
     */
    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }

    /**
     *
     * @param subscriptions
     * ArrayList of strings representing experiments the user is subscribed to
     */
    public void setSubscriptions(ArrayList<String> subscriptions) {
        this.subscriptions = (ArrayList<String>) subscriptions.clone();
    }

    /**
     *
     * @param name
     * experiment name
     */
    public void addSubscription(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.subscriptions.add(name);
        db.collection("Users").document(getUid()).update("subscriptionList", getSubscriptions());
    }

    /**
     *
     * @param name
     * experiment name
     */
    public void removeSubscription(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.subscriptions.remove(name);
        db.collection("Users").document(getUid()).update("subscriptionList", getSubscriptions());
    }
}
