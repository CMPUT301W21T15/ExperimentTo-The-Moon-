package com.example.Experiment_To_The_Moon;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
    private User mockUser(){
        return new User("wk1234", "email: wk1234@wkuniverse.com");
    }

    @Test
    void testGetUserID() {
        User testSubject = mockUser();
        assertEquals("wk1234", testSubject.getUid());
    }

    @Test
    void testGetContactInfo() {
        User testSubject = mockUser();
        assertEquals("email: wk1234@wkuniverse.com", testSubject.getContactInfo());
    }

    @Test
    void testSetContactInfo() {
        User testSubject = mockUser();
        testSubject.setContactInfo("email: wk@wkuniverse.com");
        assertEquals("email: wk@wkuniverse.com", testSubject.getContactInfo());
    }

    @Test
    void testAddSubscription(){
        User testSubject = mockUser();
        testSubject.addSubscription("Exp01");
        ArrayList<String> subscriptions = testSubject.getSubscriptions();
        assertEquals("Exp01", subscriptions.get(0));
    }

    @Test
    void testGetSubscriptions(){
        User testSubject = mockUser();
        testSubject.addSubscription("Exp01");
        testSubject.addSubscription("Exp02");
        testSubject.addSubscription("Exp03");

        ArrayList<String> subscriptions = testSubject.getSubscriptions();
        ArrayList<String> expectedSubscriptions = new ArrayList<String>();
        expectedSubscriptions.add("Exp01");
        expectedSubscriptions.add("Exp02");
        expectedSubscriptions.add("Exp03");

        assertTrue(expectedSubscriptions.equals(subscriptions));

    }

    @Test
    void testSetSubscriptions(){
        User testSubject = mockUser();
        ArrayList<String> subscriptions = new ArrayList<String>();
        subscriptions.add("Exp01");
        subscriptions.add("Exp02");
        testSubject.setSubscriptions(subscriptions);
        assertTrue(subscriptions.equals(testSubject.getSubscriptions()));
    }

    @Test
    void testRemoveSubscription(){
        User testSubject = mockUser();
        testSubject.addSubscription("Exp01");
        testSubject.removeSubscription("Exp01");
        assertFalse(testSubject.getSubscriptions().contains("Exp01"));
    }
}
