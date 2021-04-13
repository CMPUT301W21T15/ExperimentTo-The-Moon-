package com.example.Experiment_To_The_Moon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * This class is an activity that displays experiment search results
 */
public class SearchActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private ArrayAdapter<Experiment> searchListAdapter;
    private ArrayList<Experiment> searchDataList;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String searchKey = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView displayText = findViewById(R.id.search_results_tag);
        displayText.setText(String.format("Showing results for \"%s\"", searchKey));
        currentUser = (User) intent.getSerializableExtra("User");

        ListView searchList = findViewById(R.id.search_results_list);
        searchDataList = new ArrayList<>();
        searchListAdapter = new ExperimentList(this, searchDataList);
        searchList.setAdapter(searchListAdapter);

        // query the database for the searchKey
        CollectionReference colRef = db.collection("Experiments");
        db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String name = doc.getId();
                        if (name.contains(searchKey)) {
                            String owner = (String) doc.getData().get("owner");
                            String description = (String) doc.getData().get("description");
                            String is_end = (String) doc.getData().get("isEnd");
                            String region = (String) doc.getData().get("region");
                            String min_trials = (String) doc.getData().get("min_trials");
                            String type = (String) doc.getData().get("type");
                            boolean geo_location= Boolean.parseBoolean((String) doc.getData().get("geoLocation"));
                            String is_published = (String) doc.getData().get("isPublished");
                            // add the experiments from the db to experimentDataList as actual experiment objects.
                            boolean i_own_this = false;
                            try { i_own_this = owner.equals(currentUser.getUid());
                            } catch (NullPointerException f) { Log.d(TAG, "Incompatible experiment in DB"); }
                            if (is_published.equals("true") || i_own_this) {
                            try {
                                switch (type) {
                                    case "Count":
                                        searchDataList.add(new Count(name, owner, description, is_end, region, min_trials, geo_location, is_published));
                                        break;
                                    case "Binomial":
                                        searchDataList.add(new Binomial(name, owner, description, is_end, region, min_trials, geo_location, is_published));
                                        break;
                                    case "Measurement":
                                        searchDataList.add(new Measurement(name, owner, description, is_end, region, min_trials, geo_location, is_published));
                                        break;
                                    case "NonNegInt":
                                        searchDataList.add(new NonNegInt(name, owner, description, is_end, region, min_trials, geo_location, is_published));
                                        break;
                                }
                            } catch (NullPointerException a) {
                                Log.d("ADDEXP", "Incompatible experiment in DB");
                            }
                        } else {
                            Log.d("GETDOC", "Cached get failed: ", task.getException());
                        }
                    }
                    searchListAdapter.notifyDataSetChanged();
                }
            }
        });

        searchList.setOnItemClickListener((parent, view, position, id) -> {
            selectExperiment(position);
        });


    }

    private void selectExperiment(int position) {
        Intent intent = new Intent(this, ExperimentActivity.class);
        intent.putExtra("Experiment", searchDataList.get(position));  // pass in the experiment object
        intent.putExtra("type", searchDataList.get(position).getType());  // pass in the type of experiment
        intent.putExtra("User", currentUser);
        startActivityForResult(intent, 101);
    }
}
