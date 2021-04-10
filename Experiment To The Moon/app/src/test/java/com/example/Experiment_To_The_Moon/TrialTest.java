package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrialTest {
    private Trial mockTrial(){
        return new Trial("10", "w12345", "Count", "Exp01");
    }

    @Test
    public void testGetName(){
        Trial testSubject = mockTrial();
        assertEquals(testSubject.getName(), "Exp01");
    }

    @Test
    public void testGetCreator(){
        Trial testSubject = mockTrial();
        assertEquals("w12345", testSubject.getCreated_by());
    }

    @Test
    public void testSetCreator(){
        Trial testSubject = mockTrial();
        testSubject.setCreated_by("win123");
        assertEquals("win123", testSubject.getCreated_by());
    }

    @Test
    public void testSetLocation(){
        Trial testSubject = mockTrial();
        double[] location = {53.5501, -113.4687};
        testSubject.setLocation(53.5501, -113.4687);
        assertEquals(location[0], testSubject.getLocation()[0], 0);
        assertEquals(location[1], testSubject.getLocation()[1], 0);
    }

    @Test
    public void testGetType(){
        Trial testSubject = mockTrial();
        assertEquals("Count", testSubject.getType());
    }
}
