package com.example.malmond_trialbook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class AddExperimentFragment extends DialogFragment {
    private TextView date;
    private EditText description;
    private OnFragmentInteractionListener listener;
    private DatePickerDialog dpd;

    public interface OnFragmentInteractionListener {
        void onOkPressed(Experiment newExperiment);
    }

    @Override
    public void onAttach(Context context) {
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
        date = view.findViewById(R.id.date_editText);
        description = view.findViewById(R.id.description_edit_text);
        /*
         *  Calendar code used from Atif Pervaiz
         *  https://www.youtube.com/watch?v=-mJmScTAWyQ
         *  https://www.youtube.com/channel/UCT132T980-1lhm0hcZFy4ZA
         */

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        date.setText(year + "-" + (month+1) + "-" + day);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Experiment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String myDate = date.getText().toString();
                        String myDescription = description.getText().toString();
                        listener.onOkPressed(new Experiment(myDate, myDescription));
                    }}).create();
    }
}
