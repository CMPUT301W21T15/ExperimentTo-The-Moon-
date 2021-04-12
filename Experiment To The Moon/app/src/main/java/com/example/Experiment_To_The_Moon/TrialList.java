package com.example.Experiment_To_The_Moon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// custom list for individual trials
public class TrialList extends ArrayAdapter<Trial> {

    private final ArrayList<Trial> trials;
    private final Context context;

    public TrialList(Context context, ArrayList<Trial> trials) {
        super(context, 0, trials);
        this.trials = trials;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.view_all_trials_frag_content,parent,false);
        }

        Trial trial = trials.get(position);

        TextView trial_maker = view.findViewById(R.id.user_id_view_trials);
        TextView trial_results = view.findViewById(R.id.trial_results);
        TextView trial_date = view.findViewById(R.id.trial_date);
        TextView trial_location = view.findViewById(R.id.trial_location);

        trial_maker.setText("Made by: " + trial.getCreated_by());  // who created the trial.
        trial_date.setText("Created on: " + trial.getDate());
        trial_location.setText("Submitted at: " + trial.getLocation());

        /* depending on the type of trial, the results of that trial will be formatted differently.
           so we take into account all 4 types */
        if ("Measurement".equals(trial.getType())) {
            trial_results.setText("Result: " + Double.toString(trial.getMeasurementData()));
        } else if ("Binomial".equals(trial.getType())) {
            trial_results.setText("Result: " + Boolean.toString(trial.getBinomialData()));
        } else if ("NonNegInt".equals(trial.getType())) {
            trial_results.setText("Result: " + Integer.toString(trial.getNonNegIntData()));
        } else if ("Count".equals(trial.getType())) {
            trial_results.setText("Result: " + Integer.toString(trial.getCountData()));
        }

        return view;
    }
}


