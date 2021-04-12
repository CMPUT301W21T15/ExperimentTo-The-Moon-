package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Arrays;

/**
 * This class is used to display the blacklist fragment
 */
public class blacklistFragment extends DialogFragment {
    View view;
    Context arraycontext;
    private String TAG = "Sample";
    private EditText idTextbox;
    private blacklistListener listener;
    ListView list;
    ArrayAdapter<String > listAdapter;
    String EXPname;
    ArrayList<String> idList;
    String temp;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.blacklist_fragment,null);
        idTextbox=view.findViewById(R.id.inputUserId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        list= view.findViewById((R.id.blacklist_list));
        EXPname=listener.getExperimentName();
        idList= new ArrayList<String>();
        listAdapter= new ArrayAdapter<>(getContext(),R.layout.blacklist_layout,idList);
        list.setAdapter(listAdapter);
       final CollectionReference dataBase= FirebaseFirestore.getInstance().collection("Experiments").document(EXPname).collection("Blacklist");
        dataBase.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if(doc.exists()){
                    Log.d(TAG, "in for loop");
                    String temp = doc.getId();
                    Log.d(TAG, temp);
                    idList.add(temp);}
                }
                listAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud.

            }
        });


        return builder
                .setView(view)
                .setTitle("Blacklist")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ban", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String temp=idTextbox.getText().toString();

                        listener.addBlacklist(temp);
                    }
                })
                .create();
    }

    public interface blacklistListener{
        void addBlacklist(String toBan);
        String getExperimentName();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        arraycontext=context;
        try{
            listener = (blacklistListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement blacklistListener");
        }
    }

}
