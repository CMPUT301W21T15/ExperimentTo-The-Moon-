package com.example.Experiment_To_The_Moon;

/**
 * This class represents a statistics objects for a binomial experiment
 */
public class BinomialStats extends Statistics{
    //private int totalPass;
    //private int totalFail;
    //private int totalTrials;
    private float percentPass;
    //private float percentFail;
    private Experiment currentExperiment;

    /**
     *
     * @param e
     * the binomial experiment
     */
    public BinomialStats(Experiment e){
        super(e);
        currentExperiment = e;
    }

    /**
     * @deprecated
     */
    public void calcPercentPass(){
        
    }

}
