package com.example.Experiment_To_The_Moon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {
    private Experiment mockExperiment(){
        Experiment experiment = new Count("Exp01", "c56789", "Mock experiment",
                "false", "Edmonton", "10", false);
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

    @Test
    void testRenewStats(){
        Experiment experiment = mockExperiment();
        Trial trial = mockTrial();
        Trial trial1 = new Trial("70", "w12345", "Count", "Exp01");
        Trial trial2 = new Trial("40", "f62345", "Count", "Exp01");
        Trial trial3 = new Trial("40", "j92345", "Count", "Exp01");
        experiment.addResult(trial);
        experiment.addResult(trial1);
        experiment.addResult(trial2);
        experiment.addResult(trial3);
        Statistics testSubject = mockStatistics(experiment);

        testSubject.renewStats();

        assertEquals(10.0, testSubject.getMin(), 0.1);
        assertEquals(70.0, testSubject.getMax(), 0.1);
        assertEquals(40.0, testSubject.getMean(), 0.1);
        assertEquals(40.0, testSubject.getMedian(), 0.1);
        assertEquals(25.0, testSubject.getQ1(), 0.1);
        assertEquals(55.0, testSubject.getQ3(), 0.1);
        assertEquals(4, testSubject.getTotalTrials());
    }
}
