/* updateExperiment(...) and onActivityResult(...) code with changes cited from p.matthew13 @https://stackoverflow.com/users/5571700/p-mathew13 from stackoverflow.com
   @https://stackoverflow.com/a/55867656
   CC BY-SA 4.0 @https://creativecommons.org/licenses/by-sa/4.0/
*/
package com.example.mholmstr_trialbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddExperimentFragment.OnFragmentInteractionListener{

    private ArrayAdapter<Experiment> experimentAdapter;
    private ArrayList<Experiment> experimentDataList;
    private int experimentPosition;  // position of interesting experiment in the ArrayList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // the MainActivity class handles the main activity of the application
        ListView experimentList = findViewById(R.id.experiment_list);
        experimentDataList = new ArrayList<>();
        experimentAdapter = new ExperimentList(this, experimentDataList);

        experimentList.setAdapter(experimentAdapter);

        Button addExperimentButton = findViewById(R.id.add_experiment_button);
        addExperimentButton.setOnClickListener(view ->
                new AddExperimentFragment().show(getSupportFragmentManager(), "ADD_EXPERIMENT"));

        experimentList.setOnItemClickListener((parent, view, position, id) -> {  // click an experiment to edit
            updateExperiment(position);
        });

        experimentList.setOnItemLongClickListener((parent, view, position, id) -> {  // long click an experiment to delete
            experimentDataList.remove(position);  // removing the experiment clicked on
            experimentAdapter.notifyDataSetChanged(); // update adapter
            return true;
        });

    }

    @Override
    public void onOkPressed(Experiment newExperiment) {  // adding a new experiment
        experimentAdapter.add(newExperiment);
    }

    private void updateExperiment(int position) {
        Intent intent = new Intent(this, ExperimentActivity.class);
        intent.putExtra("Experiment", experimentDataList.get(position));
        experimentPosition = position;
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if(resultCode == Activity.RESULT_OK) {
                Experiment experiment = (Experiment) data.getSerializableExtra("Experiment");
                experimentPosition = data.getIntExtra("Position", 0);
                experimentDataList.set(experimentPosition, experiment);
                experimentAdapter.notifyDataSetChanged(); // update adapter
            }
        }
    }
}