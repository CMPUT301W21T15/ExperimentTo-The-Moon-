package com.example.Experiment_To_The_Moon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;

public class Answer extends AppCompatActivity implements AddAnswer.OnFragmentInteractionListener{
        ListView answerList;
        ArrayAdapter<Post> answerAdapter;
        ArrayList<Post> expAnsList;
        private Integer item_position;
        private String user;
        private FirebaseFirestore db;
        private String TAG="Sample";
        private Integer parent;
        private String name;
        private Integer number=0;
        private String parent_question;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.qanda_main);

            answerList=findViewById(R.id.QandA);

            expAnsList = new ArrayList<>();

            answerAdapter= new CustomQAndA(this, expAnsList);

            answerList.setAdapter(answerAdapter);

            db = FirebaseFirestore.getInstance();

            //final CollectionReference collectionReference = db.collection("Experiments");

            Intent intent=getIntent();
            user=(String) intent.getSerializableExtra("UserId");
            parent=(Integer) intent.getSerializableExtra("Question");
            name=(String) intent.getSerializableExtra("name");

            parent_question=parent.toString();
            final FloatingActionButton addExperimentButton = findViewById(R.id.add_post);
            addExperimentButton.setOnClickListener((v)-> {
                new AddAnswer("None",user).show(getSupportFragmentManager(), "ADD_Post");
            });

            Button back_button=findViewById(R.id.back_button);
            back_button.setOnClickListener((v) -> {
                onBackPressed();
            });


            final CollectionReference collectionReference = db.collection("Experiments").document(name).collection("QandA").document(parent_question).collection("Answers");;
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    // clear the old list
                    expAnsList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){

                        String UserId=(String) doc.getData().get("UserId");
                        String body = (String) doc.getData().get("body");
                        String isQuestion = (String) doc.getData().get("isQuestion");
                        String parent  = (String) doc.getData().get("parent");
                        expAnsList.add(new Post(UserId,body,Boolean.valueOf(isQuestion),parent));
                    }
                    answerAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
                }
            });


            answerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//add answer on Click
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    item_position=position+1;
                    String parent_pos= item_position.toString();

                    new AddAnswer(parent_pos,user).show(getSupportFragmentManager(), "ADD_Answer");


                }

            });


        }



        @Override
        public void onOkPressedAdd(Post posts){

            //postAdapter.insert(posts,item_position+1);

            // get the firestore database
            db = FirebaseFirestore.getInstance();
            final CollectionReference experimentsCollection = db.collection("Experiments").document(name).collection("QandA").document(parent_question).collection("Answers");
            HashMap<String, String> data = new HashMap<>();

            //  add new experiment info to hashmap. For now, everything is a string.
            data.put("UserId", posts.getUserID());
            data.put("body", posts.getPost());
            data.put("isQuestion", String.valueOf(posts.isQuestion()));
            data.put("parent", posts.getParent());


            number++;
            String answer=number.toString();
            // Create the new experiment document, and add the data.
            experimentsCollection

                    .document(answer)
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


    }




