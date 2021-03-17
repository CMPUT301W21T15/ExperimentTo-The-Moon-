/* updateExperiment(...) and onActivityResult(...) code with changes cited from p.matthew13 @https://stackoverflow.com/users/5571700/p-mathew13 from stackoverflow.com
   @https://stackoverflow.com/a/55867656
   CC BY-SA 4.0 @https://creativecommons.org/licenses/by-sa/4.0/
*/
package com.example.Experiment_To_The_Moon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AddExperimentFragment.OnFragmentInteractionListener, Serializable {

    private ArrayAdapter<Experiment> experimentAdapter;
    private ArrayList<Experiment> experimentDataList;
    private int experimentPosition;  // position of interesting experiment in the ArrayList
    private FirebaseFirestore db;
    private String TAG = "Sample";
    private User currentUser;
    private String firebase_id; // the device's unique id


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
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        ListView experimentList = findViewById(R.id.home_experiment_list);
        experimentDataList = new ArrayList<>();
        experimentAdapter = new ExperimentList(this, experimentDataList);

        experimentList.setAdapter(experimentAdapter);

        Button addExperimentButton = findViewById(R.id.home_add_exp_button);
        addExperimentButton.setOnClickListener(view ->
                new AddExperimentFragment().show(getSupportFragmentManager(), "ADD_EXPERIMENT"));

        // click an experiment to participate/view.
        experimentList.setOnItemClickListener((parent, view, position, id) -> {
            updateExperiment(position);
        });

        Button profileButton = findViewById(R.id.home_profile_button);
        profileButton.setOnClickListener(v -> displayProfile());

        // long click an experiment to delete
        experimentList.setOnItemLongClickListener((parent, view, position, id) -> {
            experimentDataList.remove(position);  // removing the experiment clicked on
            experimentAdapter.notifyDataSetChanged(); // update adapter
            return true;
        });

        CollectionReference collectionReference = db.collection("Experiments");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // clear the old list
                experimentDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    String name = doc.getId();
                    String description = (String) doc.getData().get("description");
                    String region = (String) doc.getData().get("region");
                    String min_trials = (String) doc.getData().get("min_trials");
                    String type = (String) doc.getData().get("type");
                    // add the experiments from the db to experimentDataList as actual experiment objects.
                    if (type.equals("Count")) {
                        experimentDataList.add(new Count(name, description, region, min_trials, false));
                    } else if (type.equals("Binomial")) {
                        experimentDataList.add(new Binomial(name, description, region, min_trials, false));
                    } else if (type.equals("Measurement")) {
                        experimentDataList.add(new Measurement(name, description, region, min_trials, false));
                    } else if (type.equals("NonNegInt")) {
                        experimentDataList.add(new NonNegInt(name, description, region, min_trials, false));
                    }
                }
                experimentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
            }
        });
    }

    @Override
    public void onOkPressed(Experiment newExperiment) {  // adding a new experiment
        experimentAdapter.add(newExperiment);

        // get the firestore database
        db = FirebaseFirestore.getInstance();
        final CollectionReference experimentsCollection = db.collection("Experiments");
        HashMap<String, String> data = new HashMap<>();

        //  add new experiment info to hashmap. For now, everything is a string.
        data.put("description", newExperiment.getDescription());
        data.put("region", newExperiment.getRegion());
        data.put("min_trials", String.valueOf(newExperiment.getMinTrials()));
        data.put("isEnd", String.valueOf(newExperiment.getIsEnd()));
        data.put("isPublished", String.valueOf(newExperiment.getIsPublished()));
        data.put("type", newExperiment.getType());

        // Create the new experiment document, and add the data.
        experimentsCollection
                .document(newExperiment.getName())
                .set(data)
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

    private void updateExperiment(int position) {
        Intent intent = new Intent(this, ExperimentActivity.class);
        intent.putExtra("Experiment", experimentDataList.get(position));  // pass in the experiment object
        intent.putExtra("type", experimentDataList.get(position).getType());  // pass in the type of experiment
        experimentPosition = position;
        startActivityForResult(intent, 101);
    }

    public void displayProfile() {
        Intent switchActivityIntent = new Intent(this, DisplayUserProfile.class);
        switchActivityIntent.putExtra("currentUser", currentUser);
        startActivityForResult(switchActivityIntent, 102);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if(resultCode == Activity.RESULT_OK) {
                Experiment experiment = (Experiment) data.getSerializableExtra("Experiment");
                onOkPressed(experiment); // quick hack to get it working because Mark is too lazy to write a new method
                experimentAdapter.notifyDataSetChanged(); // update adapter
            }
        }
        if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                currentUser = (User) data.getSerializableExtra("currentUser"); // updates current user
            }
        }
    }

}