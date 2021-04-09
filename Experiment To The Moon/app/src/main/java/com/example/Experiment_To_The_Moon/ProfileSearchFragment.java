package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class ProfileSearchFragment extends DialogFragment {
    private String myUID;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void profileOkPressed(String uid);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.profile_search_fragment,null);

        Bundle bundle = this.getArguments();
        myUID = bundle.getString("currentUser");

        EditText enterUID = view.findViewById(R.id.search_uid);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Search for a User")
                .setNegativeButton("Cancel", null)
                .setNeutralButton("My Profile", (dialogInterface, i) -> listener.profileOkPressed(myUID))
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String uid = enterUID.getText().toString();
                    if(uid.length()>0) { // We do not add anything if the field is empty.
                        listener.profileOkPressed(uid);
                    }
                }).create();
    }
}
