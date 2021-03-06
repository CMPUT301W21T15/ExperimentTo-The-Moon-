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
import android.content.Intent;
import android.media.MediaPlayer;
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


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * This class is an activity that displays experiments
 */
public class ExperimentActivity extends AppCompatActivity implements StatisticsFragment.OnFragmentInteractionListener, AddTrialFragment.DialogListener, blacklistFragment.blacklistListener, Serializable {

    // the ExperimentActivity class handles the activity in which experiments are edited
    private FirebaseFirestore db;
    private Experiment experiment;
    private User currentUser;
    public String type;
    private String geoLocation = "false";
    Statistics stats;
    private String TAG = "Sample";
    Context context;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        context=this;
        Intent intent = getIntent();
        experiment = (Experiment) intent.getSerializableExtra("Experiment");

        type = (String) intent.getSerializableExtra("type");  //  type of the experiment
        currentUser = (User) intent.getSerializableExtra("User"); // current user

        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.meep_merp);

        // cast the experiment to its proper type.
        switch (type) {
            case "Count":
                experiment = (Count) intent.getSerializableExtra("Experiment");
                break;
            case "Binomial":
                experiment = (Binomial) intent.getSerializableExtra("Experiment");
                break;
            case "Measurement":
                experiment = (Measurement) intent.getSerializableExtra("Experiment");
                break;
            case "NonNegInt":
                experiment = (NonNegInt) intent.getSerializableExtra("Experiment");
                break;
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
        mostUsefulStat.setText("");  // doesn't do anything

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

        Button end = findViewById(R.id.end_button);
        Button blacklist = findViewById(R.id.blacklist_button);
        Button viewAllTrials = findViewById(R.id.view_all_trials_button);
        Button map = findViewById(R.id.Map);
        if(!experiment.needALocation) map.setVisibility(View.INVISIBLE);
        if (!currentUser.getUid().equals(experiment.getOwner())) {
            // make description uneditable if the current user is not the owner of the experiment
            experimentDescription.setEnabled(false);
            // make delete button invisible if the current user is not the owner of the experiment
            delete.setVisibility(View.INVISIBLE);
            // make unpublish button invisible if the current user is not the owner of the experiment
            unpublish.setVisibility(View.INVISIBLE);
            // make the top row of buttons invisible if the current user is not the owner of the experiment
            end.setVisibility(View.INVISIBLE);
            blacklist.setVisibility(View.INVISIBLE);
            viewAllTrials.setVisibility(View.INVISIBLE);
            map.setVisibility(View.INVISIBLE);
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
            end.setVisibility(View.INVISIBLE);
            participate.setVisibility(View.INVISIBLE);
        } else {
            end.setText("end");
            participate.setVisibility(View.VISIBLE);
        }
        if (!experiment.getIsPublished()) participate.setVisibility(View.INVISIBLE);

        unpublish.setOnClickListener(view -> {
            experiment.togglePublish();
            if (!experiment.getIsPublished()) {
                unpublish.setText("publish");
                participate.setVisibility(View.INVISIBLE);
            } else {
                unpublish.setText("unpublish");
                if (!experiment.isEnd) participate.setVisibility(View.VISIBLE);
            }
        });

        end.setOnClickListener(view -> {
            boolean can_end = false;
            try { can_end = experiment.getTrials() >= Integer.parseInt(experiment.getMinTrials());
            } catch (Exception e) {Log.d(TAG, "Min trials not an integer"); } // EXPERIMENT NEVER ENDS :O
            if (can_end) {experiment.toggleEnd();
            } else mp.start();  // meep merp
            // Valve hereby grants, and you accept, a non-exclusive license and right, to use the Content and Services for your personal, non-commercial use (except where commercial use is expressly allowed herein or in the applicable Subscription Terms).
            // @https://store.steampowered.com/subscriber_agreement/
            if (experiment.getIsEnd()) {
                end.setVisibility(View.INVISIBLE);
                participate.setVisibility(View.INVISIBLE);
            }
        });

        stats = new Statistics(experiment);
        Button statsButton = findViewById(R.id.statistics_button);
        statsButton.setOnClickListener(view -> {
                new StatisticsFragment(stats).show(getSupportFragmentManager(), "Statistics");});

        QandA.setOnClickListener(view -> {
            Intent q_and_a=new Intent(this, Question.class);
            q_and_a.putExtra("UserId",currentUser.getUid());
            q_and_a.putExtra("Name",experiment.getName());
            startActivity(q_and_a);
        });

        participate.setOnClickListener(v -> {
            Log.d(TAG, String.valueOf(experiment.needLocation()));

            // checking experiment.needLocation() sometimes doesn't work, using db to check instead
            DocumentReference docRef = db.collection("Experiments").document(experiment.getName());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    geoLocation = (String) document.getData().get("geoLocation");
                    // was experiment.needLocation(), didn't always work. Will crash on experiments that don't have geoLocation
                    if (geoLocation.compareTo("true") == 0) {
                        // alert was found at https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android/13511580#13511580
                        // written by Mahesh https://stackoverflow.com/users/1530838/mahesh
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setMessage("You are about to participate in an Experiment that requires Location. Do you wish to continue?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Continue",
                                (dialog, id) -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("currentUser", currentUser);
                                    bundle.putBoolean("requireLoc", true);
                                    bundle.putString("trialType", experiment.getType());
                                    AddTrialFragment addTrial = new AddTrialFragment();
                                    addTrial.setArguments(bundle);
                                    addTrial.show(getSupportFragmentManager(), "AddTrial");
                                    dialog.cancel();
                                });

                        builder1.setNegativeButton(
                                "Cancel",
                                (dialog, id) -> dialog.cancel());

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("currentUser", currentUser);
                        bundle.putBoolean("requireLoc", false);
                        bundle.putString("trialType", experiment.getType());
                        AddTrialFragment addTrial = new AddTrialFragment();
                        addTrial.setArguments(bundle);
                        addTrial.show(getSupportFragmentManager(), "AddTrial");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        });

        map.setOnClickListener(view -> {
            Intent mapIntent=new Intent(this, com.example.Experiment_To_The_Moon.Map.class);
            mapIntent.putExtra("UserId",currentUser.getUid());
            mapIntent.putExtra("Name",experiment.getName());
            mapIntent.putExtra("ExperimentName",experiment.getName());
            startActivity(mapIntent);
        });

        blacklist.setOnClickListener(v -> new blacklistFragment().show(getSupportFragmentManager(),"Blacklist"));

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
        generateQR.setOnClickListener((View view) -> GenerateQRFragment.newInstance(new_name, type, currentUser.getUid()).show(getSupportFragmentManager(), "GenerateQR"));

        // build the trial list for this specific experiment.
        CollectionReference collectionReference = db
                .collection("Experiments")
                .document(experiment.getName())
                .collection("Trials");
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            // clear the old list
            experiment.clearResults();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String owner = (String) doc.getData().get("createdBy");
                String type = (String) doc.getData().get("trialType");
                String outcome;

                if (type.equals("Binomial")) {
                    outcome = Boolean.toString((Boolean) doc.getData().get("data"));
                } else {
                    outcome = Long.toString((Long) doc.getData().get("data"));
                }
                experiment.addResult(new Trial(outcome, owner, type, experiment.getName()));
            }
            totalTrials.setText("Total trials: " + experiment.getTrials()); // update the "total trials" textview.
            stats = new Statistics(experiment);
        });

    }

    /**
     * function to handle exiting the experiment activity
     */
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

    /**
     * function to subscribe/unsubscribe from an experiment
     * @param view
     * checkbox
     */
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            currentUser.addSubscription(experiment.getName());
        } else {
            currentUser.removeSubscription(experiment.getName());
        }
    }

    @Override
    public void onSubmitPress(String id, String count, String measurement, String NonNegInt, String BiNomial, ArrayList<Double> location) {
        Trial newTrial;
        int total=experiment.getTrials();
        String name=experiment.getName();
        String ExpType=experiment.getType();
        if(ExpType.equals("Count")){
            if(TextUtils.isEmpty(count))return;
            newTrial= new Trial(count,id,ExpType,name);
            if (!location.isEmpty()) {
                newTrial.setLocation(location.get(0), location.get(1));
            }
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else {
        if(ExpType.equals("NonNegInt")){
            if(TextUtils.isEmpty(NonNegInt))return;
            newTrial= new Trial(NonNegInt,id,ExpType,name);
            if (!location.isEmpty()) {
                newTrial.setLocation(location.get(0), location.get(1));
            }
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else{
        if(ExpType.equals("Measurement")){
            if(TextUtils.isEmpty(measurement))return;
            newTrial= new Trial(measurement,id,ExpType,name);
            if (!location.isEmpty()) {
                newTrial.setLocation(location.get(0), location.get(1));
            }
            if(newTrial.checkBan(id))return;
            newTrial.updateDatabase(total);
        }else{
        if(ExpType.equals("Binomial")){
            if(TextUtils.isEmpty(BiNomial))return;
            newTrial= new Trial(BiNomial,id,ExpType,name);
            if (!location.isEmpty()) {
                newTrial.setLocation(location.get(0), location.get(1));
            }
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
        Map<String, Object> data = new HashMap<>();
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

    public void startMap(View view ){

    }

}

