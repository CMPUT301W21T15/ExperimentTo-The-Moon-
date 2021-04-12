package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the Experiment class
 */
public class ExperimentTest {
    private Experiment mockExperiment(){
        Experiment experiment = new Count("Exp01", "c56789", "Mock experiment",
                "false", "Edmonton", "10", false);
        return experiment;
    }

    private Trial mockTrial(){
        return new Trial("10", "w12345", "Count", "Exp01");
    }

    @Test
    public void testTogglePublish(){
        Experiment experiment = mockExperiment();
        // Experiment.isPublish is set to false on creation
        // togglePublish should make it true
        assertFalse(experiment.getIsPublished());
        experiment.togglePublish();
        assertTrue(experiment.getIsPublished());
    }

    @Test
    public void testToggleEnd(){
        Experiment experiment = mockExperiment();
        assertFalse(experiment.getIsEnd());
        experiment.toggleEnd();
        assertTrue(experiment.getIsEnd());
    }

    @Test
    public void testGetDescription(){
        Experiment experiment = mockExperiment();
        assertEquals("Mock experiment\n", experiment.getDescription());
    }

    @Test
    public void testSetDescription(){
        Experiment experiment = mockExperiment();
        experiment.setDescription("New description \n");
        assertEquals("New description \n", experiment.getDescription());
    }
    
}
