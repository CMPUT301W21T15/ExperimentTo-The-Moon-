package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    void testTogglePublish(){
        Experiment experiment = mockExperiment();
        // Experiment.isPublish is set to false on creation
        // togglePublish should make it true
        experiment.togglePublish();
        assertTrue(experiment.getIsPublished());
    }

    @Test
    void testToggleEnd(){
        Experiment experiment = mockExperiment();
        experiment.toggleEnd();
        assertTrue(experiment.getIsEnd());
    }

    @Test
    void testGetDescription(){
        Experiment experiment = mockExperiment();
        assertEquals("Mock experiment", experiment.getDescription());
    }

    @Test
    void testSetDescription(){
        Experiment experiment = mockExperiment();
        experiment.setDescription("New description \n");
        assertEquals("New description \n", experiment.getDescription());
    }
    
}
