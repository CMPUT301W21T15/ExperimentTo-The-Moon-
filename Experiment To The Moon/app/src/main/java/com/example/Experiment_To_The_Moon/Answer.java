package com.example.Experiment_To_The_Moon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

public class Answer extends AppCompatActivity implements AddAnswer.OnFragmentInteractionListener{
        ListView answerList;
        ArrayAdapter<Post> answerAdapter;
        ArrayList<Post> expAnsList;
        private Integer item_position;
        private String user;
        private FirebaseFirestore db;
        private String TAG="Sample";
        private String parent;
        private String name;
        private String ID;
        private Integer i=0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.qanda_main);

            answerList=findViewById(R.id.QandA);

            expAnsList = new ArrayList<>();

            answerAdapter= new CustomQAndA(this, expAnsList);

            answerList.setAdapter(answerAdapter);

            db = FirebaseFirestore.getInstance();

            Intent intent=getIntent();
            user=(String) intent.getSerializableExtra("UserId");
            parent=(String) intent.getSerializableExtra("Question");
            name=(String) intent.getSerializableExtra("name");

            final FloatingActionButton addExperimentButton = findViewById(R.id.add_post);
            addExperimentButton.setOnClickListener((v)-> {
                new AddAnswer("None",user,i).show(getSupportFragmentManager(), "ADD_Post");
            });

            Button back_button=findViewById(R.id.back_button);
            back_button.setOnClickListener((v) -> {
                onBackPressed();
            });


            final CollectionReference collectionReference = db.collection("Experiments").document(name).collection("QandA").document(parent).collection("Answers");
            collectionReference.orderBy("position").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    // clear the old list
                    expAnsList.clear();
                    i=0;
                    ArrayList<Post> copy=new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){

                        String UserId=(String) doc.getData().get("userID");
                        String body = (String) doc.getData().get("post");
                        boolean isQuestion = (boolean) doc.getData().get("question");
                        String parent  = (String) doc.getData().get("parent");
                        copy.add(new Post(UserId,body,isQuestion,parent,i));
                        i++;

                    }
                    expAnsList.addAll(copy);
                    answerAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
                }
            });


            answerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//add answer on Click
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    item_position=position+1;
                    String parent_pos= item_position.toString();
                    new AddAnswer(parent_pos,user,i).show(getSupportFragmentManager(), "ADD_Answer");


                }

            });


        }



        @Override
        public void onOkPressedAdd(Post posts){

            // get the firestore database
            db = FirebaseFirestore.getInstance();
            final CollectionReference experimentsCollection = db.collection("Experiments").document(name).collection("QandA").document(parent).collection("Answers");

            ID= i.toString();
            // Create the new experiment document, and add the data.
            experimentsCollection

                    .document(ID)
                    .set(posts)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


    }




