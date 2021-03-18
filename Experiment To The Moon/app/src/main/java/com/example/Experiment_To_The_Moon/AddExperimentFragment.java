package com.example.Experiment_To_The_Moon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;


public class AddExperimentFragment<trialTypeSpinner> extends DialogFragment {
    // the AddExperimentFragment class handles the fragment which lets the user create a new experiment
    private Spinner trialTypeSpinner;

    private String name;
    private User user;
    private String description;
    private String region;
    private String min_trials; // make me an int some time.
    private String trial_type;
    private boolean geo_location;

    private OnFragmentInteractionListener listener;
    public interface OnFragmentInteractionListener {
        void onOkPressed(Experiment newExperiment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_experiment_fragment_layout, null);

        EditText experiment_name = view.findViewById(R.id.exp_name_editText);
        EditText experiment_description = view.findViewById(R.id.exp_description_editText);
        EditText experiment_region = view.findViewById(R.id.exp_region_editText);
        EditText experiment_min_trials = view.findViewById(R.id.exp_min_trials_editText);

        trialTypeSpinner = (Spinner) view.findViewById(R.id.exp_trial_type_spinner);
        populateSpinner();

        Switch geolocationSwitch = (Switch) view.findViewById(R.id.exp_geolocation_switch);

        /* This should go in an AddTrialActivity.
        CalendarView experimentDate = view.findViewById(R.id.experiment_date_calendarView);
        // getting current date from calendarView
        date = experimentDate.getDate();
        // setting a date change listener if the user selects a new date
        experimentDate.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Date calendarTime = new GregorianCalendar(year, month, dayOfMonth).getTime(); // getting another calendar with the new time
            date = calendarTime.getTime();  // converting to milliseconds
        });
         */

        Bundle bundle = this.getArguments();
        user = (User) bundle.getSerializable("Owner");
        // getting current user (i.e. the creator of the experiment) by passing around bundle

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Experiment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    name = experiment_name.getText().toString();
                    String owner = user.getUid();
                    description = experiment_description.getText().toString();
                    region = experiment_region.getText().toString();
                    min_trials = experiment_min_trials.getText().toString();
                    trial_type = trialTypeSpinner.getSelectedItem().toString();
                    geo_location = geolocationSwitch.isChecked(); // returns True or False for now.

                    if (trial_type.equals("Count")) {
                        listener.onOkPressed(new Count(name, owner, description, region, min_trials, geo_location));
                    } else if (trial_type.equals("Binomial")) {
                        listener.onOkPressed(new Binomial(name, owner, description, region, min_trials, geo_location));
                    } else if (trial_type.equals("Measurement")) {
                        listener.onOkPressed(new Measurement(name, owner, description, region, min_trials, geo_location));
                    } else if (trial_type.equals("Non-Neg Integer")) {
                        listener.onOkPressed(new NonNegInt(name, owner, description, region, min_trials, geo_location));
                    }

                }).create();

    }

    //  adds the 4 different trial types to the spinner.
    public void populateSpinner() {
        ArrayAdapter<String> trialTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_trial_types));
        trialTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trialTypeSpinner.setAdapter(trialTypeAdapter);
    }
}

