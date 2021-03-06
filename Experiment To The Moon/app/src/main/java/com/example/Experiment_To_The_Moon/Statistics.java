/**
 * Statistics
 * Original version
 * April 2021
 */

package com.example.Experiment_To_The_Moon;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * This is the class that generates the statistics for each experiment.
 */
public class Statistics {
    public float mean;
    public float median;
    public float q1;
    public float q3;
    public float stdDev;
    public int totalTrials;
    public float min;
    public float max;
    private Experiment currentExperiment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection("Experiments");
    private DocumentReference docRef;

    /**
     * This creates the Statistics object by extracting the experiment
     * from Firebase and getting the Experiment type
     * and calls renewStats() to calculate the statistics as soon
     * as the object is create
     * @param experiment
     * experiment to make statistics for
     */
    public Statistics(Experiment experiment){
        currentExperiment = experiment;
        docRef = colRef.document(experiment.getName());
        if(docRef == null){
            Log.d("CONST_DOC_REF", "The docRef variable in the constructor is null");
        }

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("DOC_MESSAGE", "DocumentSnapshot data: " + document.getData());
                        Log.d("GOTTEN_NAME", String.format("The name of the type is %s", currentExperiment.getType()));
                    } else {
                        Log.d("DOC_MESSAGE", "No such document");
                    }
                } else {
                    Log.d("DOC_MESSAGE", "get failed with ", task.getException());
                }
            }
        });

        renewStats();
    }

    /**
     * This calculates all the statistics.
     * The values are automatically set to 0 if the experiment has no trials.
     */
    public void renewStats(){
        mean = 0;
        median = 0;
        q1 = 0;
        q3 = 0;
        stdDev = 0;
        totalTrials = 0;
        min = 0; //need to initialize this properly
        max = 0;
        if(colRef == null || docRef == null){
            return;
        }
        docRef.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("SUCCESSFUL_TASK", "the task is successful");
                    if(task.getResult().isEmpty()){
                        return;
                    }
                    if(currentExperiment.getType().equals("Count") ||currentExperiment.getType().equals("NonNegInt")) {
                        int n = 0;
                        long total = 0;
                        ArrayList<Long> values = new ArrayList<Long>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            long val = (Long) doc.getData().get("data");
                            values.add(val);
                            n = n + 1;
                            total = total + val;
                        }
                        if(n == 1){
                            mean = values.get(0);
                            median = mean;
                            q1 = mean;
                            q3 = mean;
                            stdDev = 0;
                            totalTrials = 1;
                            min = mean;
                            max = mean;
                        }else{
                            Collections.sort(values);
                            totalTrials = n;
                            mean = total / n;
                            min = values.get(0);
                            max = values.get(values.size() - 1);
                            int midpoint = n / 2;
                            if(n % 2 == 0){
                                median = (values.get(midpoint - 1) + values.get(midpoint)) / 2;
                            }else {
                                median = values.get(midpoint);
                            }
                            for(int i = 0; i < values.size(); i++){
                                stdDev += Math.pow((values.get(i) - mean), 2);
                            }
                            stdDev = stdDev/n;
                            stdDev = (float) Math.sqrt(stdDev);
                            int indexQ1 = (n + 1)/4;
                            int indexQ3 = (3*(n + 1))/4;

                            int x = n/2;

                            if(x % 2 == 0){
                                int qPoint = x/2 - 1;
                                q1 = (values.get(qPoint) + values.get(qPoint + 1)) / 2;
                                qPoint = (3*x)/2 - 1;
                                q3 = (values.get(qPoint) + values.get(qPoint + 1)) / 2;
                            }else{
                                int qPoint = x/2;
                                q1 = values.get(qPoint);
                                qPoint = (3*x)/2;
                                q3 = values.get(qPoint);
                            }
                        }
                    } else if (currentExperiment.getType().equals("Measurement")){
                        int n = 0;
                        float total = 0;
                        ArrayList<Float> values = new ArrayList<Float>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            float val = (Float) doc.getData().get("data");
                            values.add(val);
                            n = n + 1;
                            total = total + val;
                        }
                        if(n == 1){
                            mean = values.get(0);
                            median = mean;
                            q1 = mean;
                            q3 = mean;
                            stdDev = 0;
                            totalTrials = 1;
                            min = mean;
                            max = mean;
                        }else{
                            Collections.sort(values);
                            totalTrials = n;
                            mean = total / n;
                            min = values.get(0);
                            max = values.get(values.size() - 1);
                            int midpoint = n / 2;
                            if(n % 2 == 0){
                                median = (values.get(midpoint - 1) + values.get(midpoint)) / 2;
                            }else {
                                median = values.get(midpoint);
                            }
                            for(int i = 0; i < values.size(); i++){
                                stdDev += Math.pow((values.get(i) - mean), 2);
                            }
                            stdDev = stdDev/n;
                            stdDev = (float) Math.sqrt(stdDev);
                            int indexQ1 = (n + 1)/4;
                            int indexQ3 = (3*(n + 1))/4;

                            int x = n/2;

                            if(x % 2 == 0){
                                int qPoint = x/2 - 1;
                                q1 = (values.get(qPoint) + values.get(qPoint + 1)) / 2;
                                qPoint = (3*x)/2 - 1;
                                q3 = (values.get(qPoint) + values.get(qPoint + 1)) / 2;
                            }else{
                                int qPoint = x/2;
                                q1 = values.get(qPoint);
                                qPoint = (3*x)/2;
                                q3 = values.get(qPoint);
                            }
                        }
                    } else if (currentExperiment.getType().equals("Binomial")){
                        int n = 0;
                        float total = 0;
                        ArrayList<Integer> values = new ArrayList<Integer>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Boolean boolVal = (Boolean) doc.getData().get("data");
                            int val;
                            if(boolVal){
                                val = 1;
                            } else {
                                val = 0;
                            }
                            values.add(val);
                            n = n + 1;
                            total = total + val;
                        }
                        if(n == 1){
                            mean = values.get(0);
                            median = mean;
                            q1 = mean;
                            q3 = mean;
                            stdDev = 0;
                            totalTrials = 1;
                            min = mean;
                            max = mean;
                        }else{
                            Collections.sort(values);
                            totalTrials = n;
                            mean = total / n;
                            min = values.get(0);
                            max = values.get(values.size() - 1);
                            int midpoint = n / 2;
                            if(n % 2 == 0){
                                median = (values.get(midpoint - 1) + values.get(midpoint)) / 2;
                            }else {
                                median = values.get(midpoint);
                            }
                            for(int i = 0; i < values.size(); i++){
                                stdDev += Math.pow((values.get(i) - mean), 2);
                            }
                            stdDev = stdDev/n;
                            stdDev = (float) Math.sqrt(stdDev);
                            int indexQ1 = (n + 1)/4;
                            int indexQ3 = (3*(n + 1))/4;

                            int x = n/2;

                            if(x % 2 == 0){
                                int qPoint = x/2 - 1;
                                q1 = (values.get(qPoint) + values.get(qPoint + 1)) / 2;
                                qPoint = (3*x)/2 - 1;
                                q3 = (values.get(qPoint) + values.get(qPoint + 1)) / 2;
                            }else{
                                int qPoint = x/2;
                                q1 = values.get(qPoint);
                                qPoint = (3*x)/2;
                                q3 = values.get(qPoint);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     *
     * @return
     * docref
     */
    public DocumentReference getDocRef(){
        return docRef;
    }

    /**
     *
     * @return
     * experiment type
     */
    public String getExpType(){
        return currentExperiment.getType();
    }

    /**
     *
     * @return
     * number of trials in the experiment
     */
    public int getNumTrials(){
        return totalTrials;
    }

    /**
     * @deprecated
     */
    public float getMax(){
        return max;
    }
    /**
     * @deprecated
     */
    public float getMean() {
        return mean;
    }
    /**
     * @deprecated
     */
    public float getMedian() {
        return median;
    }
    /**
     * @deprecated
     */
    public float getQ1() {
        return q1;
    }
    /**
     * @deprecated
     */
    public float getQ3() {
        return q3;
    }
    /**
     * @deprecated
     */
    public float getStdDev() {
        return stdDev;
    }
    /**
     * @deprecated
     */
    public int getTotalTrials() {
        return totalTrials;
    }
    /**
     * @deprecated
     */
    public float getMin() {
        return min;
    }
    /**
     * @deprecated
     */
    public Experiment getCurrentExperiment() {
        return currentExperiment;
    }
}
