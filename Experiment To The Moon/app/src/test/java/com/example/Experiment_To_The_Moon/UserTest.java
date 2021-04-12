package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the User class
 */
public class UserTest {
    private User mockUser(){
        return new User("wk1234", "email: wk1234@wkuniverse.com");
    }

    @Test
    public void testGetUserID() {
        User testSubject = mockUser();
        assertEquals("wk1234", testSubject.getUid());
    }

    @Test
    public void testGetContactInfo() {
        User testSubject = mockUser();
        assertEquals("email: wk1234@wkuniverse.com", testSubject.getContactInfo());
    }

    @Test
    public void testSetContactInfo() {
        User testSubject = mockUser();
        testSubject.setContactInfo("email: wk@wkuniverse.com");
        assertEquals("email: wk@wkuniverse.com", testSubject.getContactInfo());
    }
    @Test
    public void testSetSubscriptions(){
        User testSubject = mockUser();
        ArrayList<String> subscriptions = new ArrayList<String>();
        subscriptions.add("Exp01");
        subscriptions.add("Exp02");
        testSubject.setSubscriptions(subscriptions);
        assertEquals(subscriptions, testSubject.getSubscriptions());
    }
}
