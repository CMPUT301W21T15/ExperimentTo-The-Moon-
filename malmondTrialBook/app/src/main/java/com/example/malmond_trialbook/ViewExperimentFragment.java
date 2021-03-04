package com.example.malmond_trialbook;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewExperimentFragment extends Fragment {

    TextView currentDate;
    EditText currentDescription;
    TextView currentTrials;
    TextView currentRate;
    Button addSuccess;
    Button addFail;
    ListView trialList;
    ArrayList<String> results;
    ArrayAdapter arrayAdapter;

    Experiment currentExperiment;

    public static ViewExperimentFragment newInstance(Experiment experiment) {
        Bundle args = new Bundle();
        args.putSerializable("experiment", experiment);

        ViewExperimentFragment fragment = new ViewExperimentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_experiment_fragment_layout, container, false);
        if (getArguments() != null) {
            currentExperiment = (Experiment) getArguments().getSerializable("experiment");
        }

        trialList = v.findViewById(R.id.trial_list);
        results = currentExperiment.getTrials();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, results);
        trialList.setAdapter(arrayAdapter);
        currentDate = v.findViewById(R.id.date_view_text);
        currentDate.setText(currentExperiment.getDate());
        currentDescription = v.findViewById(R.id.description_view_text);
        currentDescription.setText(currentExperiment.getDescription());
        currentTrials = v.findViewById(R.id.trials_text);
        if (currentExperiment.getTrials().isEmpty()) {
            currentTrials.setText("Current # of trials is: 0"); // to prevent reading a null value
        } else {
            setTrials();
        }

        currentRate = v.findViewById(R.id.success_rate_text);
        currentRate.setText(currentExperiment.getSuccessRate());

        addSuccess = v.findViewById(R.id.success_button);

        addSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExperiment.addSuccess();
                setTrials();
                currentRate.setText(currentExperiment.getSuccessRate());
                arrayAdapter.notifyDataSetChanged();
            }
        });

        addFail = v.findViewById(R.id.fail_button);
        addFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExperiment.addFail();
                setTrials();
                currentRate.setText(currentExperiment.getSuccessRate());
                arrayAdapter.notifyDataSetChanged();
            }
        });

        /*
         * setOnEditorActionListener code from Abhilash Reddy
         * https://stackoverflow.com/users/2618723/abhilash-reddy
         * at https://stackoverflow.com/
         * https://stackoverflow.com/questions/16708146/android-setoneditoractionlistener-doesnt-fire
         *  CC BY-SA 3.0
         */
        currentDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    currentExperiment.setDescription(currentDescription.getText().toString());
                    if (currentExperiment.getDescription().length() < 1) {
                        currentExperiment.setDescription("Sample Text");
                    }
                }
                return false;
            }
        });

        currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 *  Calendar code used from Atif Pervaiz
                 *  https://www.youtube.com/watch?v=-mJmScTAWyQ
                 *  https://www.youtube.com/channel/UCT132T980-1lhm0hcZFy4ZA
                 */
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String newDate = (year + "-" + (month+1) + "-" + day);
                        currentExperiment.setDate(newDate);
                        currentDate.setText(newDate);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        if (v.findViewById(R.id.homepage) != null) {
            v.findViewById(R.id.homepage).setVisibility(View.INVISIBLE); // impromptu solution to activity showing underneath fragment
        }

        return v;
    }
    public void setTrials() {
        String newNumber = Integer.toString(currentExperiment.getTrials().size());
        String newString = "Current # of trials is: " + newNumber;
        currentTrials.setText(newString);
    }

}
