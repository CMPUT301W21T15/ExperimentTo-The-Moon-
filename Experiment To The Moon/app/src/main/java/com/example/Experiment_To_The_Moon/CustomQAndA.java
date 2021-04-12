package com.example.Experiment_To_The_Moon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This class is a custom array adapter for Q&A posts
 */
public class CustomQAndA extends ArrayAdapter<Post> {
    private ArrayList<Post> posts;
    private Context context;

    /**
     *
     * @param context
     * ArrayAdapter context
     * @param posts
     * ArrayList of posts to be adapted
     */
    public CustomQAndA(Context context, ArrayList<Post> posts) {
        super(context,0, posts);
        this.posts = posts;
        this.context = context;

    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // return super.getView(position, convertView, parent);
        View view=convertView;

        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.qanda_content,parent,false);
        }

        Post qs= posts.get(position);
        String parent_position=qs.getParent();


        TextView post= view.findViewById(R.id.posts);
        TextView userId=view.findViewById(R.id.user_ID);
        if(qs.isQuestion()){
            userId.setText("("+qs.getUserID()+") ");
            post.setText("Q. "+qs.getPost());
        }

        else {

            if(qs.getParent().equals("None")){

                userId.setText("("+qs.getUserID()+") ");
                post.setText(qs.getPost());
            }
            else{

                int parent_post=Integer.parseInt(parent_position);
                userId.setText("("+qs.getUserID()+") ");
                post.setText("@"+posts.get(parent_post-1).getUserID()+" "+ qs.getPost());
            }


        }

        return view;
    }
}
