package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.graph.Graph;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collections;

import static com.jjoe64.graphview.GridLabelRenderer.GridStyle.NONE;

public class PlotFragment extends DialogFragment {
    private Statistics stats;
    private PointsGraphSeries<DataPoint> series;
    private DocumentReference docRef;
    private String expType;
    private int num;

    public PlotFragment(Statistics statistics){
        stats = statistics;
        docRef = statistics.getDocRef();
        expType = statistics.getExpType();
        //num = statistics.getNumTrials();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.plot_fragment_layout, null);

        drawPlot(view);

        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Plot")
                .setNegativeButton("Close", null)
                .create();
    }

    public void drawPlot(View view){
        docRef.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("SUCCESSFUL_TASK", "the task is successful");
                    if (task.getResult().isEmpty()) {
                        return;
                    }
                    GraphView plot = view.findViewById(R.id.plot);
                    series = new PointsGraphSeries<DataPoint>();
                    int n = 0;
                    if(expType.equals("Count") || expType.equals("NonNegInt")) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            long val = (Long) doc.getData().get("data");
                            series.appendData(new DataPoint(n, val), true, 100);
                            n = n + 1;
                        }
                    } else if (expType.equals("Measurement")){
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            float val = (Float) doc.getData().get("data");
                            series.appendData(new DataPoint(n, val), true, 100);
                            n = n + 1;
                        }
                    } else if (expType.equals("Binomial")) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Boolean boolVal = (Boolean) doc.getData().get("data");
                            int val;
                            if (boolVal) {
                                val = 1;
                            } else {
                                val = 0;
                            }
                            series.appendData(new DataPoint(n, val), true, 100);
                            n = n + 1;
                        }
                        plot.getViewport().setYAxisBoundsManual(true);
                        plot.getViewport().setMinY(0);
                        plot.getViewport().setMaxY(1);
                        plot.getGridLabelRenderer().setNumHorizontalLabels(2);
                    }
                    plot.addSeries(series);
                    plot.getViewport().setScalable(true);
                    plot.getViewport().setScrollable(true);
                    plot.getViewport().setScalableY(true);
                    plot.getViewport().setScrollableY(true);

                }
            }
        });
    }
}
