/* onBackPressed() code with changes cited from p.matthew13 @https://stackoverflow.com/users/5571700/p-mathew13 from stackoverflow.com
   @https://stackoverflow.com/a/55867656
   CC BY-SA 4.0 @https://creativecommons.org/licenses/by-sa/4.0/

   setOnEditorActionListener(...) code with changes cited from Reno @https://stackoverflow.com/users/68805/reno from stackoverflow.com
   @https://stackoverflow.com/a/8063533
   CC BY-SA 3.0 @https://creativecommons.org/licenses/by-sa/3.0/
*/
package com.example.Experiment_To_The_Moon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.GregorianCalendar;

public class ExperimentActivity extends AppCompatActivity implements StatisticsFragment.OnFragmentInteractionListener{
    // the ExperimentActivity class handles the activity in which experiments are edited
    private Experiment experiment;
    Statistics stats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Sample", "WE MADE IT");

        Intent intent = getIntent();
        experiment = (Count) intent.getSerializableExtra("Experiment");
        setContentView(R.layout.activity_experiment);

        EditText experimentDescription = findViewById(R.id.edit_experiment_description_editText);
        experimentDescription.setText(experiment.getDescription());

        Button successButton = findViewById(R.id.successButton);
        successButton.setText(String.valueOf(experiment.getSuccesses()));

        Button failureButton = findViewById(R.id.failureButton);
        failureButton.setText(String.valueOf(experiment.getFailures()));

        TextView summary = findViewById(R.id.experiment_summary);
        summary.setText(experiment.getSummary());

        Button QandA=findViewById(R.id.questions_button);

        stats = new Statistics(experiment);
        Button statsButton = findViewById(R.id.statistics_button);
        statsButton.setOnClickListener(view ->
                new StatisticsFragment(stats).show(getSupportFragmentManager(), "Statistics"));

        /*  An experiment as a whole does not have a date
        experimentDate.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Date calendarTime = new GregorianCalendar(year, month, dayOfMonth).getTime(); // getting another calendar with the new time
            experiment.setDate(calendarTime.getTime()); // converting to milliseconds
        });
        */

        successButton.setOnClickListener(view -> {
            experiment.addSuccess();
            successButton.setText(String.valueOf(experiment.getSuccesses()));
            summary.setText(experiment.getSummary());
        });

        failureButton.setOnClickListener(view -> {
            experiment.addFailure();
            failureButton.setText(String.valueOf(experiment.getFailures()));
            summary.setText(experiment.getSummary());
        });

        QandA.setOnClickListener(view -> {
            Intent q_and_a=new Intent(this, QAndA.class);
            //intent.putExtra("City",cityAdapter.getItem(position).toString());
            startActivity(q_and_a);
        });

        experimentDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    experiment.setDescription(experimentDescription.getText().toString());
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        });



    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Experiment", experiment);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onPlotPressed(Experiment currentExperiment) {

    }

    @Override
    public void onHistogramPressed(Experiment currentExperiment) {

    }
}
