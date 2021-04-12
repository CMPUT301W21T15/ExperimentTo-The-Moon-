/* updateExperiment(...) and onActivityResult(...) code with changes cited from p.matthew13 @https://stackoverflow.com/users/5571700/p-mathew13 from stackoverflow.com
   @https://stackoverflow.com/a/55867656
   CC BY-SA 4.0 @https://creativecommons.org/licenses/by-sa/4.0/
*/
package com.example.Experiment_To_The_Moon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * This class represents the main activity of the program
 */
public class MainActivity extends AppCompatActivity implements AddExperimentFragment.OnFragmentInteractionListener, ProfileSearchFragment.OnFragmentInteractionListener, Serializable {

    private ArrayAdapter<Experiment> experimentAdapter;
    private ArrayAdapter<Experiment> subscribedExperimentAdapter;
    private ArrayList<Experiment> experimentDataList;
    private ArrayList<Experiment> subscribedExperimentDataList;
    private ArrayList<Barcode> myBarcodesList;
    private FirebaseFirestore db;
    private final String TAG = "Sample";
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
                } else {
                    // If device ID is not in collection, add it
                    Log.d(TAG, "No such document");
                    currentUser = new User(firebase_id, "Please edit your contact info.");
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("contactInfo", currentUser.getContactInfo());
                    data.put("userId", currentUser.getUid());
                    data.put("subscriptionList", currentUser.getSubscriptions());
                    db.collection("Users").document(firebase_id).set(data);
                }
                updateSubscribedList();
                subscribedExperimentAdapter.notifyDataSetChanged();
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
        myBarcodesList = new ArrayList<>();

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
        experimentList.setOnItemClickListener((parent, view, position, id) -> updateExperiment(position, "experimentList"));

        // click an experiment to participate/view.
        subscribedExperimentList.setOnItemClickListener((parent, view, position, id) -> updateExperiment(position, "subscribedExperimentList"));

        Button profileButton = findViewById(R.id.home_profile_button);
        profileButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("currentUser", currentUser.getUid());
            ProfileSearchFragment profile_search = new ProfileSearchFragment();
            profile_search.setArguments(bundle);
            // passing owner to fragment in a bundle
            profile_search.show(getSupportFragmentManager(), "PROFILE_SEARCH");
        });

        // QR/Bar code scanner.
        // sends results to onActivityResult (request code 49374)
        Button scanQRButton = findViewById(R.id.scan_qr_button);
        scanQRButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setPrompt("Scan a QR/Bar code");
            integrator.setOrientationLocked(false);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.initiateScan();
        });

        // click on the "GO" button to search
        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(this::search);

        CollectionReference collectionReference = db.collection("Experiments");
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                boolean geo_location= Boolean.parseBoolean((String) doc.getData().get("geoLocation"));
                // add the experiments from the db to experimentDataList as actual experiment objects.
                try {
                    switch (type) {
                        case "Count":
                            experimentDataList.add(new Count(name, owner, description, is_end, region, min_trials, geo_location));
                            break;
                        case "Binomial":
                            experimentDataList.add(new Binomial(name, owner, description, is_end, region, min_trials, geo_location));
                            break;
                        case "Measurement":
                            experimentDataList.add(new Measurement(name, owner, description, is_end, region, min_trials, geo_location));
                            break;
                        case "NonNegInt":
                            experimentDataList.add(new NonNegInt(name, owner, description, is_end, region, min_trials, geo_location));
                            break;
                    }
                } catch (NullPointerException a) {Log.d(TAG, "Incompatible experiment in DB"); } // just ignore it
            }
            experimentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
        });

    }

    /**
     * update the remote database with local changes made to an experiment
     * @param experiment
     * experiment to update
     */
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
        data.put("geoLocation" , String.valueOf(experiment.needLocation()));

        // Create the new experiment document, and add the data.
        experimentsCollection
                .document(experiment.getName())
                .set(data, SetOptions.merge()) // merging to not overwrite things accidentally
                .addOnSuccessListener(aVoid -> {
                    // These are a method which gets executed when the task is successful.
                    Log.d(TAG, "Experiment addition successful");

                })
                .addOnFailureListener(e -> {
                    // This method gets executed if there is any problem.
                    Log.d(TAG, "Experiment addition failed" + e.toString());
                });
    }

    /**
     * delete an experiment from the remote database
     * @param experiment
     * experiment to delete
     */
    private void deleteFirebase(Experiment experiment) {
        // get the firestore database
        db = FirebaseFirestore.getInstance();
        final CollectionReference experimentsCollection = db.collection("Experiments");
        experimentsCollection.document(experiment.getName()).delete();  // name is UID for now
        updateSubscribedList();
        subscribedExperimentAdapter.notifyDataSetChanged();
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
        // position of interesting experiment in the ArrayList
        startActivityForResult(intent, 101);
    }

    /**
     * display a user profile
     * @param uid
     * uid of the profile to display
     */
    public void displayProfile(String uid) {
        Intent switchActivityIntent = new Intent(this, DisplayUserProfile.class);
        switchActivityIntent.putExtra("currentUser", currentUser.getUid());
        switchActivityIntent.putExtra("lookupUser", uid);
        startActivity(switchActivityIntent);
    }

    /**
     * update list of experiments the current user is subscribed to
     */
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
        } else if (requestCode == 49374) {  // this is the result from scanning a qr code.
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    buildBarcodeList(result.getContents());
                }
            }
        }
    }

    /**
     * search for an experiment
     * @param view
     * current view
     */
    public void search(View view){
        EditText searchTerm = findViewById(R.id.home_search_bar);
        String searchKey = searchTerm.getText().toString();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(EXTRA_MESSAGE, searchKey);
        intent.putExtra("User", currentUser);
        startActivity(intent);
    }

    /**
     * This functions builds the local list of barcodes that the user has registered.
     * The barcodes are kept in Firebase Inside the Users collection. They are retrieved and added to the local
     * list, myBarcodesList. Then later, we check if the newly scanned code is in the myBarcodesList.
     * Once the list has been built, addNewTrial() is called to continue the process.
     * @param data
     * bar or QR code
     */
    private void buildBarcodeList(String data) {

        CollectionReference collectionReference = db.collection("Users")
                .document(currentUser.getUid())
                .collection("Barcodes");

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    // clear the old list, just in case.
                    myBarcodesList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String code = doc.getId();
                        String name = (String) doc.getData().get("name");
                        String result = (String) doc.getData().get("result");
                        String type = (String) doc.getData().get("type");

                        myBarcodesList.add(new Barcode(code, name, result, type)); // add each barcode to the local list.
                    }
                    addNewTrial(data);
                });
    }

    /**
     * called from inside addNewTrial.
     * @param result
     * unprocessed barcode trial result
     * @return
     * processed barcode trial result
     */
    public String handleBarCodes(String result) {
        boolean isBarcode = false;
        String name = "";
        String type = "";
        String outcome = "";

        for (int i = 0; i < myBarcodesList.size(); i++) {
            Barcode barcode = myBarcodesList.get(i);

            if (result.equals(barcode.getCode())) {
                isBarcode = true;
                name = barcode.getExperiment_name();
                type = barcode.getType();
                outcome = barcode.getResult();
            }
        }

        if (!isBarcode) {
            return result;
        } else {
            return name + "," + type + "," + outcome;
        }
    }

    /**
     * This function adds a new trial to a specific experiment after scanning a QR/Bar code.
     * If the QR/Bar code is not registered, the try/catch block will print an error message and return.
     * If the QR/Bar code is registered, the target experiment will be updated locally, and in the Firebase.
     * @param data
     * bar or QR code
     */
    public void addNewTrial(String data) {

        // handleBarCodes checks if the newly scanned code is a Bar or QR code, and then formats the data accordingly.
        data = handleBarCodes(data);

        String name;
        String type;
        String result;
        try {
            StringTokenizer tokenizer = new StringTokenizer(data, ",");
            // parse up the input data. It was comma separated.
            name = tokenizer.nextToken(); // name of experiment
            type = tokenizer.nextToken(); // type of experiment
            result = tokenizer.nextToken(); // desired result from QR/bar scan. e.g. pass/fail
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "The code that you scanned is not registered.", Toast.LENGTH_LONG).show();
            return;
        }

        /* Had to get a snapshot of the db to find the total amount of trials for the
           specific experiment, and then add the new trial at the end.
        */
        db.collection("Experiments")
                .document(name)
                .collection("Trials")
                .get()
                .addOnCompleteListener(task -> {
                    int pos = -1;

                    // find the experiment in the experimentDataList
                    for (int i = 0; i < experimentDataList.size(); i++) {
                        Experiment temp_exp = experimentDataList.get(i);
                        if (temp_exp.getName().equals(name)) {
                            pos = i;
                        }
                    }

                    // check for QR codes that have been registered, but the experiment has been closed.
                    // pos == -1 if there is no experiment in experimentDataList that matches the input name.
                    if (pos == -1) {
                        Toast.makeText(getApplicationContext(), "The Experiment for this code no longer accepting trials.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Experiment final_exp = experimentDataList.get(pos);
                    final_exp.clearResults(); // clear the old result list.

                    // re-add all of the trials to the result list.
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String new_createdBy = (String) doc.getData().get("createdBy");
                        String new_result = doc.getData().get("data").toString();
                        String date = (String) doc.getData().get("date");

                        Trial newTrial = new Trial(new_result, new_createdBy, type, name);
                        newTrial.setDate(date);
                        final_exp.addResult(newTrial);
                    }

                    // add the new trial, finally.
                    int total = final_exp.getTrials();
                    Trial newTrial2 = new Trial(result, currentUser.getUid(), type, name);
                    newTrial2.updateDatabase(total); // add to db.
                });
    }

    @Override
    public void profileOkPressed(String uid) {
        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.requireNonNull(document).exists()) {
                    // if UID exists, display profile
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    displayProfile(uid);
                } else {
                    // If it does not exist, display error
                    Log.d(TAG, "No such document");
                    Toast toast = Toast.makeText(this,"UID does not exist", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast toast = Toast.makeText(this,"UID does not exist", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
