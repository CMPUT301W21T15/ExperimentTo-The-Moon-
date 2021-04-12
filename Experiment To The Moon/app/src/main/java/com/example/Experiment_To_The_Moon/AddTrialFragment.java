package com.example.Experiment_To_The_Moon;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

/**
 * This class is used for the inner workings of the Participation Fragment so that Trials can be created
 */
public class AddTrialFragment extends DialogFragment {
    //Text box that holds the UserID
    private TextView id;
    //Text box that holds a string of the integer for a count experiment
    private EditText count;
    //Text box that holds a String of the Float for a Measurement experiment
    private EditText measurement;
    //Text box that holds a string of an int that is used for a non negative int experiment
    private EditText NonNegInt;
    //Contains the chosen String for a binomial experiment
    private String biNomial;
    // Contains latitude for geo-loc enabled experiments
    private EditText latitude;
    // Contains longitude for geo-loc enabled experiments
    private EditText longitude;
    // used for accessing last known location
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private DialogListener listener;
    private Spinner tfSpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_trials_fragment, null);
        createSpinner(view);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        id = view.findViewById(R.id.trial_uid_edittext);
        User currentUser = (User) getArguments().getSerializable("currentUser");
        boolean requireLoc = getArguments().getBoolean("requireLoc");
        String trialType = getArguments().getString("trialType");
        id.setText(currentUser.getUid());
        count = view.findViewById(R.id.count_result);
        TextView count_textbox = view.findViewById(R.id.count_textbox);
        NonNegInt = view.findViewById(R.id.nonnegint_result);
        TextView nonnegint_textbox = view.findViewById(R.id.nonnegint_textbox);
        measurement = view.findViewById(R.id.measurement_result);
        TextView measurement_textbox = view.findViewById(R.id.measurement_textbox);
        tfSpinner = view.findViewById(R.id.spinner);
        TextView binomial_textbox = view.findViewById(R.id.binomial_textbox);
        latitude = view.findViewById(R.id.latitude_coordinate);
        TextView latitude_textbox = view.findViewById(R.id.latitude_textbox);
        longitude = view.findViewById(R.id.longitude_coordinate);
        TextView longitude_textbox = view.findViewById(R.id.longitude_textbox);

        // toggle visibility for unused trial types
        switch (trialType) {
            case "NonNegInt":
                count.setVisibility(View.INVISIBLE);
                count_textbox.setVisibility(View.INVISIBLE);
                measurement.setVisibility(View.INVISIBLE);
                measurement_textbox.setVisibility(View.INVISIBLE);
                tfSpinner.setVisibility(View.INVISIBLE);
                binomial_textbox.setVisibility(View.INVISIBLE);
                nonnegint_textbox.setVisibility(View.VISIBLE);
                NonNegInt.setVisibility(View.VISIBLE);
                break;

            case "Binomial":
                count.setVisibility(View.INVISIBLE);
                count_textbox.setVisibility(View.INVISIBLE);
                measurement.setVisibility(View.INVISIBLE);
                measurement_textbox.setVisibility(View.INVISIBLE);
                tfSpinner.setVisibility(View.VISIBLE);
                binomial_textbox.setVisibility(View.VISIBLE);
                nonnegint_textbox.setVisibility(View.INVISIBLE);
                NonNegInt.setVisibility(View.INVISIBLE);
                break;

            case "Measurement":
                count.setVisibility(View.INVISIBLE);
                count_textbox.setVisibility(View.INVISIBLE);
                measurement.setVisibility(View.VISIBLE);
                measurement_textbox.setVisibility(View.VISIBLE);
                tfSpinner.setVisibility(View.INVISIBLE);
                binomial_textbox.setVisibility(View.INVISIBLE);
                nonnegint_textbox.setVisibility(View.INVISIBLE);
                NonNegInt.setVisibility(View.INVISIBLE);
                break;

            case "Count":
                count.setVisibility(View.VISIBLE);
                count_textbox.setVisibility(View.VISIBLE);
                measurement.setVisibility(View.INVISIBLE);
                measurement_textbox.setVisibility(View.INVISIBLE);
                tfSpinner.setVisibility(View.INVISIBLE);
                binomial_textbox.setVisibility(View.INVISIBLE);
                nonnegint_textbox.setVisibility(View.INVISIBLE);
                NonNegInt.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }

        // Only show geoloc stuff when enabled
        if (requireLoc) {
            latitude_textbox.setEnabled(true);
            latitude_textbox.setVisibility(View.VISIBLE);
            longitude_textbox.setEnabled(true);
            longitude_textbox.setVisibility(View.VISIBLE);
            latitude.setEnabled(true);
            latitude.setClickable(true);
            latitude.setVisibility(View.VISIBLE);
            longitude.setEnabled(true);
            longitude.setClickable(true);
            longitude.setVisibility(View.VISIBLE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        } else {
            latitude_textbox.setEnabled(false);
            latitude_textbox.setVisibility(View.INVISIBLE);
            longitude_textbox.setEnabled(false);
            longitude_textbox.setVisibility(View.INVISIBLE);
            latitude.setEnabled(false);
            latitude.setClickable(false);
            latitude.setVisibility(View.INVISIBLE);
            longitude.setEnabled(false);
            longitude.setClickable(false);
            longitude.setVisibility(View.INVISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Add Trial")
                .setNegativeButton("Close", (dialog, which) -> {

                })
                .setPositiveButton("Submit", (dialog, which) -> {
                    String tempCount = count.getText().toString();
                    String tempMeasurement = measurement.getText().toString();
                    String tempNonNegInt = NonNegInt.getText().toString();
                    String tempId = id.getText().toString();
                    String currentLatitude = latitude.getText().toString();
                    String currentLongitude = longitude.getText().toString();
                    ArrayList<Double> location = new ArrayList<>();
                    if (currentLatitude.length() > 0 && currentLongitude.length() > 0) {
                        location.add(0, Double.parseDouble(currentLatitude));
                        location.add(1, Double.parseDouble(currentLongitude));
                    }
                    biNomial = tfSpinner.getSelectedItem().toString();
                    listener.onSubmitPress(tempId, tempCount, tempMeasurement, tempNonNegInt, biNomial, location);
                })
                .create();
    }

    /**
     * This method updates the longitude and latitude (ie location) of the trial
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                String stringLat = String.format("%1$s", location.getLatitude());
                String stringLong = String.format("%1$s", location.getLongitude());
                latitude.setText(stringLat);
                longitude.setText(stringLong);
            }
        });
    }

    public interface DialogListener {
        public void onSubmitPress(String id, String count, String measurement, String NonNegInt, String BiNomial, ArrayList<Double> location);
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
        tfSpinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> trialTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_boolean_types));
        trialTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tfSpinner.setAdapter(trialTypeAdapter);
    }
}
