package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// Fragment to be accessed from ExperimentActivity. View all trials of the current Experiment
public class ViewAllTrialsFragment extends DialogFragment {

    private String name;
    private ArrayAdapter<Trial> trialListAdapter;
    private ArrayList<Trial> trialDataList;
    ListView trialListView;

    // input: name of the experiment.
    public ViewAllTrialsFragment(String new_name) {
        name = new_name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_all_trials_fragment, null);

        trialDataList = new ArrayList<>();
        trialListAdapter = new TrialList(getContext(), trialDataList);

        trialListView = view.findViewById(R.id.trial_list);
        trialListView.setAdapter(trialListAdapter);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db
                .collection("Experiments")
                .document(name)
                .collection("Trials");

        // Idea: create a list of all trials from Firebase, display trials in a ListView.
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // clear the old list
                trialDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    String owner = (String) doc.getData().get("createdBy");
                    String type = (String) doc.getData().get("trialType");
                    String exp_name = name;

                    // dealing with binomial trials turned out to be a challenge. I just had to do some ugly
                    // type checking and converting to make it work.
                    // End result: new Trial object is added to the list.
                    if (type.equals("Binomial")) {
                        Boolean outcome = doc.getBoolean("data");
                        String outcome_string = Boolean.toString(outcome);

                        if (outcome_string.equals("true")) {
                            trialDataList.add(new Trial("Pass", owner, type, exp_name));
                        } else if (outcome_string.equals("false")) {
                            trialDataList.add(new Trial("Fail", owner, type, exp_name));
                        } else {
                            throw new NullPointerException("Binomial trial has null outcome");
                        }
                    } else {
                        String outcome = doc.getData().get("data").toString();
                        trialDataList.add(new Trial(outcome, owner, type, exp_name));
                    }
                }
                trialListAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("View Trials")
                .setNegativeButton("Cancel", null)
                .create();
    }

}
