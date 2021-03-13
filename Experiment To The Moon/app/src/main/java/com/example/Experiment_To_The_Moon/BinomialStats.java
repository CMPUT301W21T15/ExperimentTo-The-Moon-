package com.example.Experiment_To_The_Moon;

public class BinomialStats extends Statistics{
    //private int totalPass;
    //private int totalFail;
    //private int totalTrials;
    private float percentPass;
    //private float percentFail;
    private Experiment currentExperiment;

    public BinomialStats(Experiment e){
        super(e);
        currentExperiment = e;
    }

    public void calcPercentPass(){
        
    }

}
