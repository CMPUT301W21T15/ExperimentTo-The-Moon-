package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

//This class is used for the inner workings of the Participation Fragment so that Trials can be created
public class AddTrialFragment extends DialogFragment{
    //Text box that holds the UserID
    private TextView id;
    //Text box that holds a string of the integer for a count experiment
    private EditText count;
    //Text box that holds a String of the Float for a Measurement experiment
    private EditText measurement;
    //Text box that holds a string of an int that is used for a non negative int experiment
    private EditText NonNegInt;
    //Contains the chosen String for a binomial experiment
    private FirebaseFirestore db;
    private String biNomial;
    private DialogListener listener;
    private Spinner tfSpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_trials_fragment,null);
        createSpinner(view);
        id= view.findViewById(R.id.textInputEditText);
        String firebase_id = FirebaseInstallations.getInstance().getId().toString(); // this is the firebase ID associated with the unique app installation ID
        firebase_id = firebase_id.substring(33); // only looking for the 7 digit ID
        id.setText(firebase_id);
        count= view.findViewById(R.id.textInputEditText2);
        NonNegInt= view.findViewById(R.id.textInputEditText3);
        measurement= view.findViewById(R.id.textInputEditText4);
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Add Trial")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tempCount=count.getText().toString();
                        String tempMeasurement=measurement.getText().toString();
                        String tempNonNegInt=NonNegInt.getText().toString();
                        String tempId=id.getText().toString();
                        biNomial=tfSpinner.getSelectedItem().toString();

                        listener.onSubmitPress(tempId,tempCount,tempMeasurement,tempNonNegInt,biNomial);
                    }
                })
                .create();
    }

    public interface DialogListener {
        public void onSubmitPress(String id, String count, String measurement, String NonNegInt, String BiNomial);

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try{
            listener= (DialogListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement listener");
        }
    }
    // Input the view that the spinner named spinner is in and it will initialize the spinner with the strings in the array spinner_boolean_types
    private void createSpinner(View view){
        tfSpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> trialTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_boolean_types));
        trialTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tfSpinner.setAdapter(trialTypeAdapter);
    }

}
