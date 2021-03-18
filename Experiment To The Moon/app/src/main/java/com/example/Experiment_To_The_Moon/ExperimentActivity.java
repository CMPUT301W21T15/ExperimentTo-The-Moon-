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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class ExperimentActivity extends AppCompatActivity implements StatisticsFragment.OnFragmentInteractionListener, addTrialFragment.DialogListener{
    // the ExperimentActivity class handles the activity in which experiments are edited
    private Experiment experiment;
    private User currentUser;
    private String type;
    Statistics stats;
    private static String ExpType ="Test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        type = (String) intent.getSerializableExtra("type");  //  type of the experiment
        currentUser = (User) intent.getSerializableExtra("User"); // current user

        // cast the experiment to it's proper type.
        if (type.equals("Count")) {
            experiment = (Count) intent.getSerializableExtra("Experiment");
        } else if (type.equals("Binomial")) {
            experiment = (Binomial) intent.getSerializableExtra("Experiment");
        } else if (type.equals("Measurement")) {
            experiment = (Measurement) intent.getSerializableExtra("Experiment");
        } else if (type.equals("NonNegInt")) {
            experiment = (NonNegInt) intent.getSerializableExtra("Experiment");
        }

        setContentView(R.layout.activity_experiment);

        EditText experimentDescription = findViewById(R.id.edit_experiment_description_editText);
        experimentDescription.setText(experiment.getDescription());

        TextView ownerStatus = findViewById(R.id.owner_status);
        if (experiment.getOwner().equals(currentUser.getUid())) {
            ownerStatus.setText("This experiment belongs to me!");
        } else ownerStatus.setText("This experiment is not mine :(");


        /* Code from old assignment that is not needed

        Button successButton = findViewById(R.id.successButton);
        successButton.setText(String.valueOf(experiment.getSuccesses()));

        Button failureButton = findViewById(R.id.failureButton);
        failureButton.setText(String.valueOf(experiment.getFailures()));

        TextView summary = findViewById(R.id.experiment_summary);
        summary.setText(experiment.getSummary());

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

        //  An experiment as a whole does not have a date
        experimentDate.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Date calendarTime = new GregorianCalendar(year, month, dayOfMonth).getTime(); // getting another calendar with the new time
            experiment.setDate(calendarTime.getTime()); // converting to milliseconds
        */
        Button QandA=findViewById(R.id.questions_button);

        Button participate= findViewById(R.id.participate_button);

        stats = new Statistics(experiment);
        Button statsButton = findViewById(R.id.statistics_button);
        statsButton.setOnClickListener(view ->
                new StatisticsFragment(stats).show(getSupportFragmentManager(), "Statistics"));

        QandA.setOnClickListener(view -> {
            Intent q_and_a=new Intent(this, QAndA.class);
            //intent.putExtra("City",cityAdapter.getItem(position).toString());
            startActivity(q_and_a);
        });

        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addTrialFragment().show(getSupportFragmentManager(),"AddTrial");
            }
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
    @Override
    public void onSubmitPress(String id, String count, String measurement, String NonNegInt, String BiNomial){
        Trial newTrial;
        int total=experiment.getTrials();
        String name=experiment.getName();
        String tempString="Experiments/";
        tempString=tempString+name;
        DocumentReference dataBase= FirebaseFirestore.getInstance().document(tempString);
        dataBase.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    ExpType = documentSnapshot.getString("type");
                }
            }
        });
        if(ExpType.equals("Count")){
            newTrial= new Trial(count,id,ExpType,name);
            newTrial.updateDatabase(total);
        }else {
        if(ExpType.equals("NonNegInt")){
            newTrial= new Trial(NonNegInt,id,ExpType,name);
            newTrial.updateDatabase(total);
        }else{
        if(ExpType.equals("Measurement")){
            newTrial= new Trial(measurement,id,ExpType,name);
            newTrial.updateDatabase(total);
        }else{
        if(ExpType.equals("Binomial")){
            newTrial= new Trial(BiNomial,id,ExpType,name);
            newTrial.updateDatabase(total);
        }else{newTrial= new Trial("","","", "");}
        }}}
        experiment.addResult(newTrial);
    }
}
