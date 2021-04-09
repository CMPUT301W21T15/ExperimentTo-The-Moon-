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
    private String expType;

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
                        expType = (String) document.getData().get("type");
                        Log.d("GOTTEN_NAME", String.format("The name of the type is %s", expType));
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
                    if(expType.equals("Count") || expType.equals("NonNegInt")) {
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
                    } else if (expType.equals("Measurement")){
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
                    } else if (expType.equals("Binomial")){
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


    public DocumentReference getDocRef(){
        return docRef;
    }

    public String getExpType(){
        return expType;
    }

    public int getNumTrials(){
        return totalTrials;
    }

    public float getMax(){
        return max;
    }
}
