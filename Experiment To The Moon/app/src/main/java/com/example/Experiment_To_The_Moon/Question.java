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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

//public class QAndA extends AppCompatActivity implements AddQuestion.OnFragmentInteractionListener,AddAnswer.OnFragmentInteractionListener{
public class Question extends AppCompatActivity implements AddQuestion.OnFragmentInteractionListener{

    ListView postList;
    ArrayAdapter<Post> postAdapter;
    ArrayList<Post> expPostsList;
    int item_position;
    String user;
    private FirebaseFirestore db;
    private String TAG="Sample";
    private String name;
    private Integer number=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qanda_main);

        postList=findViewById(R.id.QandA);

        expPostsList = new ArrayList<>();

        postAdapter= new CustomQAndA(this, expPostsList);

        postList.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();

        //final CollectionReference collectionReference = db.collection("Experiments");

        Intent intent=getIntent();
        user=(String) intent.getSerializableExtra("UserId");
        name=(String) intent.getSerializableExtra("Name");


        final FloatingActionButton addExperimentButton = findViewById(R.id.add_post);
        addExperimentButton.setOnClickListener((v)-> {
            new AddQuestion(user).show(getSupportFragmentManager(), "ADD_Post");
        });

        Button back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener((v) -> {
            onBackPressed();
        });


        final CollectionReference collectionReference = db.collection("Experiments").document(name).collection("QandA");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // clear the old list
                expPostsList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    String UserId=(String) doc.getData().get("UserId");
                    String body = (String) doc.getData().get("body");
                    String isQuestion = (String) doc.getData().get("isQuestion");
                    expPostsList.add(new Post(UserId,body,Boolean.valueOf(isQuestion)));
                }
                postAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.
            }
        });


        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//add answer on Click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item_position=position+1;

                Intent answer=new Intent(Question.this, Answer.class);
                answer.putExtra("Question",item_position);
                answer.putExtra("UserId",user);
                answer.putExtra("name",name);
                startActivity(answer);

            }

        });


    }

    @Override
    public void onOkPressed(Post posts){
        //postAdapter.add(posts);

        // get the firestore database
        db = FirebaseFirestore.getInstance();
        final CollectionReference experimentsCollection = db.collection("Experiments").document(name).collection("QandA");;
        HashMap<String, String> data = new HashMap<>();

        //  add new experiment info to hashmap. For now, everything is a string.
        data.put("UserId", posts.getUserID());
        data.put("body", posts.getPost());
        data.put("isQuestion", String.valueOf(posts.isQuestion()));

        number++;
        String question=number.toString();
        // Create the new experiment document, and add the data.
        experimentsCollection
                .document(question)
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

