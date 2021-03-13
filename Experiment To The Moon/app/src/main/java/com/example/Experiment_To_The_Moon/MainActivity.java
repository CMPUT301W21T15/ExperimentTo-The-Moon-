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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AddExperimentFragment.OnFragmentInteractionListener{

    private ArrayAdapter<Experiment> experimentAdapter;
    private ArrayList<Experiment> experimentDataList;
    private int experimentPosition;  // position of interesting experiment in the ArrayList
    private FirebaseFirestore db;
    String TAG = "Sample";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "KEK");
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);

        // the MainActivity class handles the main activity of the application
        ListView experimentList = findViewById(R.id.experiment_list);
        experimentDataList = new ArrayList<>();
        experimentAdapter = new ExperimentList(this, experimentDataList);

        experimentList.setAdapter(experimentAdapter);

        Button addExperimentButton = findViewById(R.id.add_experiment_button);
        addExperimentButton.setOnClickListener(view ->
                new AddExperimentFragment().show(getSupportFragmentManager(), "ADD_EXPERIMENT"));

        /*
        experimentList.setOnItemClickListener((parent, view, position, id) -> {  // click an experiment to edit
            updateExperiment(position);
        }); */

        experimentList.setOnItemLongClickListener((parent, view, position, id) -> {  // long click an experiment to delete
            experimentDataList.remove(position);  // removing the experiment clicked on
            experimentAdapter.notifyDataSetChanged(); // update adapter
            return true;
        });

        final CollectionReference collectionReference = db.collection("Experiments");
        Log.d(TAG, "KEK");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // clear the old list
                experimentDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    //Log.d(TAG, String.valueOf(doc.getData().get("province_name")));
                    String name = doc.getId();
                    String description = (String) doc.getData().get("description");
                    String region = (String) doc.getData().get("region");
                    String min_trials = (String) doc.getData().get("min_trials");
                    //String province = (String) doc.getData().get("province_name");
                    experimentDataList.add(new Count(name, description, region, min_trials, false) {
                    }); // Adding the cities and provinces from FireStore.
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

        // Create the new experiment document, and add the data.
        experimentsCollection
                .document(newExperiment.getName())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is successful.
                        Log.d(TAG, "Data addition successful");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // This method gets executed if there is any problem.
                        Log.d(TAG, "Data addition failed" + e.toString());
                    }
                });
    }

    /*
    private void updateExperiment(int position) {
        Intent intent = new Intent(this, ExperimentActivity.class);
        intent.putExtra("Experiment", experimentDataList.get(position));
        experimentPosition = position;
        startActivityForResult(intent, 101);
    } */

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