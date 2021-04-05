package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddAnswer extends DialogFragment {
    private EditText Body;
    private AddAnswer.OnFragmentInteractionListener listener;
    private String parent_pos;
    private String user;
    private int position;

    public AddAnswer(String position, String user,int place) {

        this.parent_pos=position;
        this.user=user;
        this.position=place;
    }


    public interface OnFragmentInteractionListener{
        void onOkPressedAdd(Post newPosts);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if(context instanceof AddAnswer.OnFragmentInteractionListener){
            listener = (AddAnswer.OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString()+"must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.qanda_add,null);
        Body= view.findViewById(R.id.body);


        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Reply")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String body= Body.getText().toString();

                        listener.onOkPressedAdd(new Post(user,body,false,parent_pos,position));

                    }
                }).create();

    }



}
