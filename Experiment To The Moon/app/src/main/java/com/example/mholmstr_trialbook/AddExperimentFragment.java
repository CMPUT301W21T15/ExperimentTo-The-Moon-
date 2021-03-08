package com.example.mholmstr_trialbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;


public class AddExperimentFragment extends DialogFragment {
    // the AddExperimentFragment class handles the fragment which lets the user create a new experiment
    private long date;
    private String description;
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
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_experiment_fragment_layout, null);
        CalendarView experimentDate = view.findViewById(R.id.experiment_date_calendarView);
        EditText experimentDescription = view.findViewById(R.id.experiment_description_editText);
        // getting current date from calendarView
        date = experimentDate.getDate();
        // setting a date change listener if the user selects a new date
        experimentDate.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Date calendarTime = new GregorianCalendar(year, month, dayOfMonth).getTime(); // getting another calendar with the new time
            date = calendarTime.getTime();  // converting to milliseconds
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Experiment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    description = experimentDescription.getText().toString();
                    listener.onOkPressed(new Experiment(date, description));
                }).create();
    }
}
