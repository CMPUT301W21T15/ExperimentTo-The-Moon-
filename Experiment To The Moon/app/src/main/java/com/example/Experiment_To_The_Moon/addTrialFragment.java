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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class addTrialFragment extends DialogFragment{
    private EditText id;
    private EditText count;
    private EditText measurement;
    private EditText NonNegInt;
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

    private void createSpinner(View view){
        tfSpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> trialTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_boolean_types));
        trialTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tfSpinner.setAdapter(trialTypeAdapter);
    }

}
