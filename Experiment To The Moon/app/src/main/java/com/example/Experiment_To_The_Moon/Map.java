package com.example.Experiment_To_The_Moon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    ArrayList<double[]>locationArrayList= new ArrayList<>();
    private String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent= getIntent();
        String name= intent.getStringExtra("ExperimentName");



        CollectionReference collectionReference = db
                .collection("Experiments")
                .document(name)
                .collection("Trials");
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                double[] tempArray = new double[2];
                String tempString=  doc.getData().get("createdBy").toString();
                 tempArray[0] = doc.getDouble("latitude");
                 tempArray[1] = doc.getDouble("longitude");
                 locationArrayList.add(tempArray);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        for(int i=0;i<locationArrayList.size();i++) {
            double[] temp = locationArrayList.get(i);
            String tempString = "Trial";//+i;
            MarkerOptions options =new MarkerOptions();
            options.position(new LatLng(temp[0], temp[1]));
            options.title(tempString);
            googleMap.addMarker(options);


        }
    }
}