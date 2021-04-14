package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @deprecated
 * Tested in IntegrationTest
 */
public class StatisticsTest {
    private Experiment mockExperiment(){
        Experiment experiment = new Count("Exp01", "c56789", "Mock experiment",
                "false", "Edmonton", "10", false, "true");
        return experiment;
    }

    private Trial mockTrial(){
        return new Trial("10", "w12345", "Count", "Exp01");
    }

    private Statistics mockStatistics(){
        Experiment experiment = mockExperiment();
        Statistics stats = new Statistics(experiment);
        return stats;
    }

    private Statistics mockStatistics(Experiment experiment){
        return new Statistics(experiment);
    }
}
