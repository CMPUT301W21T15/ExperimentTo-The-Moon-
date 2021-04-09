package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrialTest {
    private Trial mockTrial(){
        return new Trial("10", "w12345", "Count", "Exp01");
    }

    @Test
    void testGetName(){
        Trial testSubject = mockTrial();
        assertEquals(testSubject.getName(), "Exp01");
    }

    @Test
    void testGetCreator(){
        Trial testSubject = mockTrial();
        assertEquals("w12345", testSubject.getCreated_by());
    }

    @Test
    void testSetCreator(){
        Trial testSubject = mockTrial();
        testSubject.setCreated_by("win123");
        assertEquals("win123", testSubject.getCreated_by());
    }

    @Test
    void testSetLocation(){
        Trial testSubject = mockTrial();
        testSubject.setLocation("Edmonton");
        assertEquals("Edmonton", testSubject.getLocation());
    }

    @Test
    void testGetType(){
        Trial testSubject = mockTrial();
        assertEquals("Count", testSubject.getType());
    }
}
