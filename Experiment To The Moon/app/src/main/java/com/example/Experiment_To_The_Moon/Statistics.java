package com.example.Experiment_To_The_Moon;

import java.util.ArrayList;

public class Statistics {
    public float mean;
    public float median;
    public float q1;
    public float q3;
    public float stdDev;
    public int totalTrials;
    public float min;
    public float max;
    private Experiment currentExperiment;

    public Statistics(Experiment experiment){
        currentExperiment = experiment;
        renewStats();
    }

    public void renewStats(){
        calcMean();
        calcMedian();
        calcQ1();
        calcQ3();
        calcMin();
        calcMax();
        calcStdDev();
        calcTotalTrials();
    }
    public void calcMean() {
        mean = 0;
    }

    public void calcMedian() {
        median = 0;
    }

    public void calcQ1() {
        q1 = 0;
    }

    public void calcQ3() {
        q3 = 0;
    }

    public void calcStdDev() {
        stdDev = 0;
    }

    public void calcTotalTrials(){
        totalTrials = 0;
    }

    public void calcMin(){
        min = 0;
    }

    public void calcMax(){
        max = 0;
    }

    public void displayPlot(){

    }
    public void displayHistogram(){

    }
}
