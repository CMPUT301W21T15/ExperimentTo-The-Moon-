/* onBackPressed() code with changes cited from p.matthew13 @https://stackoverflow.com/users/5571700/p-mathew13 from stackoverflow.com
   @https://stackoverflow.com/a/55867656
   CC BY-SA 4.0 @https://creativecommons.org/licenses/by-sa/4.0/

   setOnEditorActionListener(...) code with changes cited from Reno @https://stackoverflow.com/users/68805/reno from stackoverflow.com
   @https://stackoverflow.com/a/8063533
   CC BY-SA 3.0 @https://creativecommons.org/licenses/by-sa/3.0/
*/
package com.example.Experiment_To_The_Moon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExperimentActivity extends AppCompatActivity implements StatisticsFragment.OnFragmentInteractionListener, AddTrialFragment.DialogListener, blacklistFragment.blacklistListener, Serializable {

    // the ExperimentActivity class handles the activity in which experiments are edited
    private Experiment experiment;
    private User currentUser;
    public String type;
    Statistics stats;
    private String TAG = "Sample";
    Context context;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        Intent intent = getIntent();
        experiment = (Experiment) intent.getSerializableExtra("Experiment");

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
        experimentName.setText("Experiment: " + experiment.getName());

        TextView ownerID = findViewById(R.id.experiment_activity_owner_id);
        ownerID.setText("Owner: " + experiment.getOwner());

        TextView experimentType = findViewById(R.id.experiment_activity_experiment_type);
        experimentType.setText("Type: " + experiment.getType());

        EditText experimentDescription = findViewById(R.id.experiment_activity_description_editText);
        experimentDescription.setText(experiment.getDescription());

        TextView minTrials = findViewById(R.id.experiment_activity_min_trials);
        minTrials.setText("Min trials: " + experiment.getMinTrials());

        TextView status = findViewById(R.id.experiment_activity_status);
        if (experiment.getIsEnd()) {
            status.setText("Experiment is over");
        } else status.setText("Experiment is not over");

        TextView region = findViewById(R.id.experiment_activity_region);
        region.setText("Region: " + experiment.getRegion());

        TextView totalTrials = findViewById(R.id.experiment_activity_total_trials);
        totalTrials.setText("Total trials: " + experiment.getTrials());

        TextView mostUsefulStat = findViewById(R.id.experiment_activity_most_useful_stat);
        mostUsefulStat.setText("PLACEHOLDER TEXT PUT MOST USEFUL STATISTIC");

        CheckBox subscribed = findViewById(R.id.subscribe_box);
        // will be checked if user is subbed to experiment
        if (currentUser.getSubscriptions() != null) {
            if (currentUser.getSubscriptions().contains(experiment.getName())) {
                subscribed.setChecked(true);
            } else {
                subscribed.setChecked(false);
            }
        } else {
            subscribed.setChecked(false);
        }

        Button delete = findViewById(R.id.delete_button);

        Button unpublish = findViewById(R.id.unpublish_button);
        Button blacklist = findViewById(R.id.blacklist_button);
        Button viewAllTrials = findViewById(R.id.view_all_trials_button);

        if (!currentUser.getUid().equals(experiment.getOwner())) {
            // make description uneditable if the current user is not the owner of the experiment
            experimentDescription.setEnabled(false);
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
        if (experiment.getIsEnd()) {
            unpublish.setText("publish");
            participate.setVisibility(View.INVISIBLE);
        } else {
            unpublish.setText("unpublish");
            participate.setVisibility(View.VISIBLE);
        }

        unpublish.setOnClickListener(view -> {
            experiment.toggleEnd();
            if (experiment.getIsEnd()) {
                unpublish.setText("publish");
                participate.setVisibility(View.INVISIBLE);
            } else {
                unpublish.setText("unpublish");
                participate.setVisibility(View.VISIBLE);
            }
        });

        stats = new Statistics(experiment);
        Button statsButton = findViewById(R.id.statistics_button);
        statsButton.setOnClickListener(view ->
                new StatisticsFragment(stats).show(getSupportFragmentManager(), "Statistics"));

        QandA.setOnClickListener(view -> {
            Intent q_and_a=new Intent(this, Question.class);
            q_and_a.putExtra("UserId",currentUser.getUid());
            q_and_a.putExtra("Name",experiment.getName());
            startActivity(q_and_a);
        });

        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(experiment.needLocation()));
                if(experiment.needLocation()) {
                    //alert was found at https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android/13511580#13511580
                    //written by Mahesh https://stackoverflow.com/users/1530838/mahesh
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("You are about to participate in a Experiment that requires Location. Do you wish to continue?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("currentUser", currentUser);
                                    AddTrialFragment addTrial = new AddTrialFragment();
                                    addTrial.setArguments(bundle);
                                    addTrial.show(getSupportFragmentManager(), "AddTrial");
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("currentUser", currentUser);
                    AddTrialFragment addTrial = new AddTrialFragment();
                    addTrial.setArguments(bundle);
                    addTrial.show(getSupportFragmentManager(), "AddTrial");
                }
            }
        });

        blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new blacklistFragment().show(getSupportFragmentManager(),"Blacklist");
            }
        });

        // implement view all trials button. opens fragment.
        String new_name = experiment.getName();
        viewAllTrials.setOnClickListener(view ->
            new ViewAllTrialsFragment(new_name).show(getSupportFragmentManager(), "ViewAllTrials"));


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

        Button generateQR = findViewById(R.id.generate_qr_fragment);
        generateQR.setOnClickListener((View view) -> {
            GenerateQRFragment.newInstance(new_name, type, currentUser.getUid()).show(getSupportFragmentManager(), "GenerateQR");
        });

    }

    // function to handle exiting the experiment activity
    public void finishExperimentActivity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Experiment", experiment);
        returnIntent.putExtra("currentUser", currentUser);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishExperimentActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishExperimentActivity();
            return true;
        }
        return false;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            currentUser.addSubscription(experiment.getName());
        } else {
            currentUser.removeSubscription(experiment.getName());
        }
    }

    @Override
    public void onSubmitPress(String id, String count, String measurement, String NonNegInt, String BiNomial){
        Trial newTrial;
        int total=experiment.getTrials();
        String name=experiment.getName();
        String ExpType=experiment.getType();
        if(ExpType.equals("Count")){
            if(TextUtils.isEmpty(count))return;;
            newTrial= new Trial(count,id,ExpType,name);
            if(experiment.checkLocation(newTrial.getLocation()))return;
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else {
        if(ExpType.equals("NonNegInt")){
            if(TextUtils.isEmpty(NonNegInt))return;;
            newTrial= new Trial(NonNegInt,id,ExpType,name);
            if(experiment.checkLocation(newTrial.getLocation()))return;
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else{
        if(ExpType.equals("Measurement")){
            if(TextUtils.isEmpty(measurement))return;;
            newTrial= new Trial(measurement,id,ExpType,name);
            if(experiment.checkLocation(newTrial.getLocation()))return;
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else{
        if(ExpType.equals("Binomial")){
            if(TextUtils.isEmpty(BiNomial))return;;
            newTrial= new Trial(BiNomial,id,ExpType,name);
            if(experiment.checkLocation(newTrial.getLocation()))return;
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else{newTrial= new Trial("","","", "");}
        }}}
        experiment.addResult(newTrial);
    }
    @Override
    public void addBlacklist(String toBan){
       if(TextUtils.isEmpty(toBan))return;
        String tempString="Experiments/";
        tempString=tempString+experiment.getName();
        String tempString2="/Blacklist/";
        tempString=tempString+tempString2;
        CollectionReference dataBase= FirebaseFirestore.getInstance().collection(tempString);
        Map<String, Object> data = new HashMap< String, Object>();
        data.put("UserID", toBan);
        dataBase
                .document(toBan)
                .set(data);
    }
    @Override
    public String getExperimentName(){
        return experiment.getName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

