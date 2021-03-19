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

import org.w3c.dom.Text;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class ExperimentActivity extends AppCompatActivity implements StatisticsFragment.OnFragmentInteractionListener, addTrialFragment.DialogListener{
    // the ExperimentActivity class handles the activity in which experiments are edited
    private Experiment experiment;
    private User currentUser;
    private String type;
    Statistics stats;
    private static String ExpType ="Test";  // we have two variables with the same function

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        type = (String) intent.getSerializableExtra("type");  //  type of the experiment
        currentUser = (User) intent.getSerializableExtra("User"); // current user

        // cast the experiment to its proper type.
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

        TextView experimentName = findViewById(R.id.experiment_activity_experiment_name);
        experimentName.setText(experiment.getName());

        TextView ownerID = findViewById(R.id.experiment_activity_owner_id);
        ownerID.setText(experiment.getOwner());

        TextView experimentType = findViewById(R.id.experiment_activity_experiment_type);
        experimentType.setText(experiment.getType());

        EditText experimentDescription = findViewById(R.id.experiment_activity_description_editText);
        experimentDescription.setText(experiment.getDescription());

        TextView minTrials = findViewById(R.id.experiment_activity_min_trials);
        minTrials.setText("Min trials: " + experiment.getMinTrials());

        TextView status = findViewById(R.id.experiment_activity_status);
        if (experiment.getIsEnd()) {
            status.setText("Experiment is over");
        }
        else status.setText("Experiment is not over");

        TextView region = findViewById(R.id.experiment_activity_region);
        region.setText(experiment.getRegion());

        TextView totalTrials = findViewById(R.id.experiment_activity_total_trials);
        totalTrials.setText("Total trials: " + experiment.getTrials());

        TextView mostUsefulStat = findViewById(R.id.experiment_activity_most_useful_stat);
        mostUsefulStat.setText("something goes here idk");

        Button delete = findViewById(R.id.delete_button);

        Button unpublish = findViewById(R.id.unpublish_button);
        Button blacklist = findViewById(R.id.blacklist_button);
        Button viewAllTrials = findViewById(R.id.view_all_trials_button);

        if (!currentUser.getUid().equals(experiment.getOwner())) {
            // make delete button invisible if the current user is not the owner of the experiment
            delete.setVisibility(View.INVISIBLE);
            // make the top row of buttons invisible if the current user is not the owner of the experiment
            unpublish.setVisibility(View.INVISIBLE);
            blacklist.setVisibility(View.INVISIBLE);
            viewAllTrials.setVisibility(View.INVISIBLE);
        }

        delete.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("Experiment", experiment);
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();  // deleting the experiment
        });

        Button QandA=findViewById(R.id.questions_button);

        Button participate= findViewById(R.id.participate_button);
        // reusing code for now, make a proper method later
        if (experiment.getIsEnd()) {
            unpublish.setText("publish");
            participate.setVisibility(View.INVISIBLE);
        }
        else {
            unpublish.setText("unpublish");
            participate.setVisibility(View.VISIBLE);
        }

        unpublish.setOnClickListener(view -> {
            // what's the difference between toggling end and unpublishing the experiment?
            experiment.toggleEnd();
            if (experiment.getIsEnd()) {
                unpublish.setText("publish");
                participate.setVisibility(View.INVISIBLE);
            }
            else {
                unpublish.setText("unpublish");
                participate.setVisibility(View.VISIBLE);
            }
        });

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
