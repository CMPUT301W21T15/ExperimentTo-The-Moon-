package com.example.Experiment_To_The_Moon;

import androidx.fragment.app.DialogFragment;

public class BinomialStatsFragment extends DialogFragment {
    private Experiment currentExperiment;
    public BinomialStatsFragment(Experiment experiment){
        currentExperiment = experiment;
    }
}
