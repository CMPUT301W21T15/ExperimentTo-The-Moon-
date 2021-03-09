package com.example.Experiment_To_The_Moon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExperimentList extends ArrayAdapter<Experiment> {
    // the ExperimentList class handles the list of experiments visible from the main activity
    private final ArrayList<Experiment> experiments;
    private final Context context;

    public ExperimentList(Context context, ArrayList<Experiment> experiments) {
        super(context, 0, experiments);
        this.experiments = experiments;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.experiment_layout,parent,false);
        }

        Experiment experiment = experiments.get(position);

        TextView experimentDate = view.findViewById(R.id.experiment_date);
        TextView experimentDescription = view.findViewById(R.id.experiment_description);

        @SuppressLint("SimpleDateFormat")
        String displayedDate = new SimpleDateFormat("yyyy-MM-dd").format(experiment.getDate());
        experimentDate.setText(displayedDate);
        experimentDescription.setText(experiment.getDescription());

        return view;
    }
}
