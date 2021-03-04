package com.example.malmond_trialbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements AddExperimentFragment.OnFragmentInteractionListener{

    ListView experimentList;
    ArrayAdapter<Experiment> experimentAdapter;
    ArrayList<Experiment> experimentDataList;
    Experiment currentExperiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        experimentList = findViewById(R.id.experiment_list);
        experimentDataList = new ArrayList<>();
        experimentAdapter = new CustomList(this, experimentDataList);
        experimentList.setAdapter(experimentAdapter);
        experimentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentExperiment = (Experiment) parent.getItemAtPosition(position);
            }
        });

        final FloatingActionButton addExperimentButton = findViewById(R.id.add_experiment_button);
        addExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddExperimentFragment().show(getSupportFragmentManager(), "ADD_EXPERIMENT");
            }
        });

        final FloatingActionButton deleteExperimentButton= findViewById(R.id.delete_experiment_button);
        deleteExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeExperiment(currentExperiment);
            }
        });

        final FloatingActionButton editExperimentButton= findViewById(R.id.edit_experiment_button);
        editExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (experimentAdapter.getPosition(currentExperiment) != -1) {
                    ViewExperimentFragment fragment = ViewExperimentFragment.newInstance(currentExperiment);
                    if (findViewById(R.id.homepage) != null) {
                        findViewById(R.id.homepage).setVisibility(View.INVISIBLE); // impromptu solution to activity showing underneath fragment
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public void onOkPressed(Experiment newExperiment) {
        if (newExperiment.getDate().length() < 1) {
            /*
             *  Calendar code used from Atif Pervaiz
             *  https://www.youtube.com/watch?v=-mJmScTAWyQ
             *  https://www.youtube.com/channel/UCT132T980-1lhm0hcZFy4ZA
             */
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            newExperiment.setDate(year + "-" + (month+1) + "-" + day);
        }
        if (newExperiment.getDescription().length() < 1) {
            newExperiment.setDescription("Sample Text");
        }
        experimentAdapter.add(newExperiment);
    }

    public void removeExperiment(Experiment experiment) {
        if (experimentAdapter.getPosition(currentExperiment) != -1) {
            experimentAdapter.remove(experiment);
        }
    }

    @Override
    public void onBackPressed()
    {
        findViewById(R.id.homepage).setVisibility(VISIBLE); // impromptu solution to activity showing underneath fragment
        experimentAdapter.notifyDataSetChanged();
        super.onBackPressed();  // optional depending on your needs
    }
}