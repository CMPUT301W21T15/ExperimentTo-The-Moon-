package com.example.Experiment_To_The_Moon;

public class User {

    private String uid;
    private String contactInfo;

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
}
