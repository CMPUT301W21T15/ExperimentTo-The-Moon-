package com.example.Experiment_To_The_Moon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class QAndA extends AppCompatActivity implements AddQuestion.OnFragmentInteractionListener,AddAnswer.OnFragmentInteractionListener{
    ListView postList;
    ArrayAdapter<Post> postAdapter;
    ArrayList<Post> expPostsList;
    int item_position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qanda_main);

        postList=findViewById(R.id.QandA);

        expPostsList = new ArrayList<>();

        postAdapter= new CustomQAndA(this, expPostsList);

        postList.setAdapter(postAdapter);

        final FloatingActionButton addExperimentButton = findViewById(R.id.add_post);
        addExperimentButton.setOnClickListener((v)-> {
            new AddQuestion().show(getSupportFragmentManager(), "ADD_Post");
        });

        Button back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener((v) -> {
            onBackPressed();
        });

//        postList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //Delete experiment on longclick
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
//                //expPostsList.remove(position);
//                if(expPostsList.get(position).isQuestion()){
//                    int i=position;
//                    while(!expPostsList.get(i).isQuestion()|expPostsList.size()==1){
//                        expPostsList.remove(i+1);
//                        //i++;
//                    }
//                    expPostsList.remove(position);
//                }
//                else{
//                    expPostsList.remove(position);
//                }
//                postAdapter.notifyDataSetChanged();
//                return false;
//            }
//
//        });

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//add answer on Click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item_position=position;

                new AddAnswer(position).show(getSupportFragmentManager(), "ADD_Answer");



            }

        });


    }

    @Override
    public void onOkPressed(Post posts){
        postAdapter.add(posts);

    }

    @Override
    public void onOkPressedAdd(Post posts){

        //Posts current=postAdapter.getItem(item_position+1);
//        Posts previous=postAdapter.getItem(item_position);
//        posts.setUserID("Answer to "+previous.getUserID()+" by "+posts.getUserID());
        //postAdapter=new CustomQAndA(this, expPostsList,item_position);
        //postAdapter.add(posts);
        postAdapter.insert(posts,item_position+1);

    }


}