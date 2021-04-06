/* updateExperiment(...) and onActivityResult(...) code with changes cited from p.matthew13 @https://stackoverflow.com/users/5571700/p-mathew13 from stackoverflow.com
   @https://stackoverflow.com/a/55867656
   CC BY-SA 4.0 @https://creativecommons.org/licenses/by-sa/4.0/
*/
package com.example.Experiment_To_The_Moon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringTokenizer;


public class MainActivity extends AppCompatActivity implements AddExperimentFragment.OnFragmentInteractionListener, Serializable {

    private ArrayAdapter<Experiment> experimentAdapter;
    private ArrayAdapter<Experiment> subscribedExperimentAdapter;
    private ArrayList<Experiment> experimentDataList;
    private ArrayList<Experiment> subscribedExperimentDataList;
    private int experimentPosition;  // position of interesting experiment in the ArrayList
    private FirebaseFirestore db;
    private String TAG = "Sample";
    private User currentUser;
    private String firebase_id; // the device's unique id
    public static final String EXTRA_MESSAGE = "com.example.Experiment_To_The_Moon.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_home_screen);

        // the MainActivity class handles the main activity of the application
        firebase_id = FirebaseInstallations.getInstance().getId().toString(); // this is the firebase ID associated with the unique app installation ID
        firebase_id = firebase_id.substring(33); // only looking for the 7 digit ID
        DocumentReference docRef = db.collection("Users").document(firebase_id);

        // check if the ID exists in the database
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.requireNonNull(document).exists()) {
                    // if it exists, get info
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    String contactInfo = (String) Objects.requireNonNull(document.getData()).get("contactInfo");
                    ArrayList<String> subs = (ArrayList<String>) document.getData().get("subscriptionList");
                    currentUser = new User(firebase_id, contactInfo);
                    currentUser.setSubscriptions(subs);
                    updateSubscribedList();
                    subscribedExperimentAdapter.notifyDataSetChanged();
                } else {
                    // If device ID is not in collection, add it
                    Log.d(TAG, "No such document");
                    currentUser = new User(firebase_id, "Please edit your contact info.");
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("contactInfo", currentUser.getContactInfo());
                    data.put("userId", currentUser.getUid());
                    data.put("subscriptionList", currentUser.getSubscriptions());
                    db.collection("Users").document(firebase_id).set(data);
                    updateSubscribedList();
                    subscribedExperimentAdapter.notifyDataSetChanged();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        ListView experimentList = findViewById(R.id.home_experiment_list);
        ListView subscribedExperimentList = findViewById(R.id.subscription_list);
        experimentDataList = new ArrayList<>();
        subscribedExperimentDataList = new ArrayList<>();
        experimentAdapter = new ExperimentList(this, experimentDataList);
        subscribedExperimentAdapter = new ExperimentList(this, subscribedExperimentDataList);

        experimentList.setAdapter(experimentAdapter);
        subscribedExperimentList.setAdapter(subscribedExperimentAdapter);

        Button addExperimentButton = findViewById(R.id.home_add_exp_button);

        addExperimentButton.setOnClickListener((View view) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Owner", currentUser);
            AddExperimentFragment add_experiment = new AddExperimentFragment();
            add_experiment.setArguments(bundle);
            // passing owner to fragment in a bundle
            add_experiment.show(getSupportFragmentManager(), "ADD_EXPERIMENT");
        });

        // click an experiment to participate/view.
        experimentList.setOnItemClickListener((parent, view, position, id) -> {
            updateExperiment(position, "experimentList");
        });

        // click an experiment to participate/view.
        subscribedExperimentList.setOnItemClickListener((parent, view, position, id) -> updateExperiment(position, "subscribedExperimentList"));

        Button profileButton = findViewById(R.id.home_profile_button);
        profileButton.setOnClickListener(v -> displayProfile());

        // QR code scanner.
        Button scanQRButton = (Button) findViewById(R.id.scan_qr_button);
        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setPrompt("Scan a QR code");
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        // click on the "GO" button to search
        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> search(v));

        CollectionReference collectionReference = db.collection("Experiments");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // clear the old list
                experimentDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String name = doc.getId();
                    String owner = (String) doc.getData().get("owner");
                    String description = (String) doc.getData().get("description");
                    String is_end = (String) doc.getData().get("isEnd");
                    String region = (String) doc.getData().get("region");
                    String min_trials = (String) doc.getData().get("min_trials");
                    String type = (String) doc.getData().get("type");
                    // add the experiments from the db to experimentDataList as actual experiment objects.
                    try {
                        if (type.equals("Count")) {
                            experimentDataList.add(new Count(name, owner, description, is_end, region, min_trials, false));
                        } else if (type.equals("Binomial")) {
                            experimentDataList.add(new Binomial(name, owner, description, is_end, region, min_trials, false));
                        } else if (type.equals("Measurement")) {
                            experimentDataList.add(new Measurement(name, owner, description, is_end, region, min_trials, false));
                        } else if (type.equals("NonNegInt")) {
                            experimentDataList.add(new NonNegInt(name, owner, description, is_end, region, min_trials, false));
                        }
                    } catch (NullPointerException a) {Log.d(TAG, "Incompatible experiment in DB"); } // just ignore it
                }
                experimentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
            }
        });

    }

    private void syncFirebase(Experiment experiment) {

        // get the firestore database
        db = FirebaseFirestore.getInstance();
        final CollectionReference experimentsCollection = db.collection("Experiments");
        HashMap<String, String> data = new HashMap<>();

        //  add experiment info to hashmap. For now, everything is a string.
        data.put("owner", experiment.getOwner());
        data.put("description", experiment.getDescription());
        data.put("region", experiment.getRegion());
        data.put("min_trials", String.valueOf(experiment.getMinTrials()));
        data.put("isEnd", String.valueOf(experiment.getIsEnd()));
        data.put("isPublished", String.valueOf(experiment.getIsPublished()));
        data.put("type", experiment.getType());

        // Create the new experiment document, and add the data.
        experimentsCollection
                .document(experiment.getName())
                .set(data, SetOptions.merge()) // merging to not overwrite things accidentally
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is successful.
                        Log.d(TAG, "Experiment addition successful");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // This method gets executed if there is any problem.
                        Log.d(TAG, "Experiment addition failed" + e.toString());
                    }
                });
    }

    private void addSubscriptionToFirebase(){

    }

    private void deleteFirebase(Experiment experiment) {
        // get the firestore database
        db = FirebaseFirestore.getInstance();
        final CollectionReference experimentsCollection = db.collection("Experiments");
        experimentsCollection.document(experiment.getName()).delete();  // name is UID for now
    }


    @Override
    public void onOkPressed(Experiment newExperiment) {  // adding a new experiment
        experimentAdapter.add(newExperiment);
        syncFirebase(newExperiment);
    }

    private void updateExperiment(int position, String passedBy) {
        Intent intent = new Intent(this, ExperimentActivity.class);
        if (passedBy.equals("experimentList")) {
            intent.putExtra("Experiment", experimentDataList.get(position));  // pass in the experiment object
            intent.putExtra("type", experimentDataList.get(position).getType());  // pass in the type of experiment
        } else if (passedBy.equals("subscribedExperimentList")) {
            intent.putExtra("Experiment", subscribedExperimentDataList.get(position));  // pass in the experiment object
            intent.putExtra("type", subscribedExperimentDataList.get(position).getType());  // pass in the type of experiment
        }
        intent.putExtra("User", currentUser);
        experimentPosition = position;
        startActivityForResult(intent, 101);
    }

    public void displayProfile() {
        Intent switchActivityIntent = new Intent(this, DisplayUserProfile.class);
        switchActivityIntent.putExtra("currentUser", currentUser);
        startActivityForResult(switchActivityIntent, 102);
    }

    public void updateSubscribedList() {
        // clear the old list
        subscribedExperimentDataList.clear();
        // populate subbed experiments list
        for (int i = 0; i < currentUser.getSubscriptions().size(); i++) {
            String subbedExperimentName = currentUser.getSubscriptions().get(i);
            DocumentReference currentSubbedExperiment = db.collection("Experiments").document(subbedExperimentName);
            currentSubbedExperiment.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        // if it exists, get info
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String name = document.getId();
                        String owner = (String) document.getData().get("owner");
                        String description = (String) document.getData().get("description");
                        String is_end = (String) document.getData().get("isEnd");
                        String region = (String) document.getData().get("region");
                        String min_trials = (String) document.getData().get("min_trials");
                        String type = (String) document.getData().get("type");
                        switch (type) {
                            case "Count":
                                subscribedExperimentDataList.add(new Count(name, owner, description, is_end, region, min_trials, false));
                                break;
                            case "Binomial":
                                subscribedExperimentDataList.add(new Binomial(name, owner, description, is_end, region, min_trials, false));
                                break;
                            case "Measurement":
                                subscribedExperimentDataList.add(new Measurement(name, owner, description, is_end, region, min_trials, false));
                                break;
                            case "NonNegInt":
                                subscribedExperimentDataList.add(new NonNegInt(name, owner, description, is_end, region, min_trials, false));
                                break;
                            default:
                                break;
                        }
                        subscribedExperimentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Experiment experiment = (Experiment) data.getSerializableExtra("Experiment");
                syncFirebase(experiment);
                experimentAdapter.notifyDataSetChanged(); // update adapter
                currentUser = (User) data.getSerializableExtra("currentUser"); // updates current user
                updateSubscribedList();
                subscribedExperimentAdapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    Experiment experiment = (Experiment) data.getSerializableExtra("Experiment");
                    deleteFirebase(experiment);
                    experimentAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                currentUser = (User) data.getSerializableExtra("currentUser"); // updates current user
            }
        } else if (requestCode == 49374) {  // this is the result from scanning a qr code.
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                addNewTrial(result.getContents()); // add the new trial to the proper experiment & db.
            }
        }

    }

    public void search(View view){
        EditText searchTerm = (EditText) findViewById(R.id.home_search_bar);
        String searchKey = searchTerm.getText().toString();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(EXTRA_MESSAGE, searchKey);
        intent.putExtra("User", currentUser);
        startActivity(intent);
    }

    public void addNewTrial(String data) {
        StringTokenizer tokenizer = new StringTokenizer(data, ",");

        // parse up the input data. It was comma separated.
        String name = tokenizer.nextToken(); // name of experiment
        String type = tokenizer.nextToken(); // type of experiment
        String result = tokenizer.nextToken(); // desired result from QR scan. e.g. pass/fail

        /* Had to get a snapshot of the db to find the total amount of trials for the
           specific experiment, and then add the new trial at the end.
        */
        db.collection("Experiments")
                .document(name)
                .collection("Trials")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int pos = -1;

                        // find the experiment in the experimentDataList
                        for (int i = 0; i < experimentDataList.size(); i++) {
                            Experiment temp_exp = experimentDataList.get(i);
                            if (temp_exp.getName().equals(name)) {
                                pos = i;
                            }
                        }

                        // check for bad QR codes
                        // pos == -1 if there is no experiment in experimentDataList that matches the input name.
                        if (pos == -1) {
                            Toast.makeText(getApplicationContext(), "Invalid QR code or Nonexistent experiment", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Experiment final_exp = experimentDataList.get(pos);
                        final_exp.clearResults(); // clear the old result list.

                        // re-add all of the trials to the result list.
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String new_createdBy = (String) doc.getData().get("createdBy");
                            String new_result = doc.getData().get("data").toString();
                            Trial newTrial = new Trial(new_result, new_createdBy, type, name);
                            final_exp.addResult(newTrial);
                        }

                        // add the new trial, finally.
                        int total = final_exp.getTrials();
                        Trial newTrial2 = new Trial(result, currentUser.getUid(), type, name);
                        newTrial2.updateDatabase(total); // add to db.
                    }
                });
    }
}