package com.example.Experiment_To_The_Moon;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StatisticsFragment extends DialogFragment {
    private Experiment currentExperiment;

    public StatisticsFragment(Experiment experiment){
        currentExperiment = experiment;
    }

    public interface OnFragmentInteractionListener {
        void onPlotPressed();
        void onHistogramPressed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.statistics_fragment_layout, null);
        return super.onCreateDialog(savedInstanceState);
    }
}
