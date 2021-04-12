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
/**
 * This class is a custom array adapter for experiments
 */
public class ExperimentList extends ArrayAdapter<Experiment> {
    // the ExperimentList class handles the list of experiments visible from the main activity
    private final ArrayList<Experiment> experiments;
    private final Context context;

    /**
     *
     * @param context
     * ArrayAdapter context
     * @param experiments
     * ArrayList of experiments to be adapted
     */
    public ExperimentList(Context context, ArrayList<Experiment> experiments) {
        super(context, 0, experiments);
        this.experiments = experiments;
        this.context = context;
    }

    /**
     * UI method
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.experiment_layout,parent,false);
        }

        Experiment experiment = experiments.get(position);

        TextView experimentName = view.findViewById(R.id.experiment_name);
        TextView experimentDescription = view.findViewById(R.id.experiment_description);
        TextView experimentOwner = view.findViewById(R.id.experiment_owner_username);
        TextView experimentStatus = view.findViewById(R.id.experiment_status);

        experimentName.setText(experiment.getName());
        experimentDescription.setText(experiment.getDescription());
        experimentOwner.setText(experiment.getOwner());
        String status = "Ongoing";
        if(experiment.getIsEnd()){
            status = "Ended";
        }
        experimentStatus.setText(status);

        return view;
    }
}
