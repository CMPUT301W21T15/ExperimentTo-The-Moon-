package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

public class StatisticsFragment extends DialogFragment {
    // This is the fragment that appears when a user taps on the statistics button
    private Statistics stats;
    Button plotButton;
    Button histogramButton;
    private OnFragmentInteractionListener listener;

    TextView mean;
    TextView median;
    TextView q1;
    TextView q3;
    TextView stdDev;
    TextView totalTrials;
    TextView min;
    TextView max;

    // The constructor takes the current experiment as an argument
    public StatisticsFragment(Statistics currentStatistics){
        currentStatistics.renewStats();
        stats = currentStatistics;
    }

    public interface OnFragmentInteractionListener {
        void onPlotPressed(Experiment currentExperiment);
        void onHistogramPressed(Experiment currentExperiment);
    }

    public void setStats(){
        mean.setText(Float.toString(stats.mean));
        median.setText(Float.toString(stats.median));
        q1.setText(Float.toString(stats.q1));
        q3.setText(Float.toString(stats.q3));
        stdDev.setText(Float.toString(stats.stdDev));
        totalTrials.setText(Float.toString(stats.totalTrials));
        min.setText(Float.toString(stats.min));
        max.setText(Float.toString(stats.max));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.statistics_fragment_layout, null);

        histogramButton = view.findViewById(R.id.histogram_button);
        histogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HistogramFragment(stats).show(getChildFragmentManager(), "Histogram");
            }
        });
        plotButton = view.findViewById(R.id.plot_button);
        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlotFragment(stats).show(getChildFragmentManager(), "Plot");
            }
        });

        mean = view.findViewById(R.id.textViewMeanVal);
        median = view.findViewById(R.id.textViewMedianVal);
        q1 = view.findViewById(R.id.textViewQ1Val);
        q3 = view.findViewById(R.id.textViewQ3Val);
        stdDev = view.findViewById(R.id.textViewStdVal);
        totalTrials = view.findViewById(R.id.textViewTotalVal);
        min = view.findViewById(R.id.textViewMinVal);
        max = view.findViewById(R.id.textViewMaxVal);

        setStats();

        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Statistics")
                .setNegativeButton("Cancel", null)
                .create();
    }
}
