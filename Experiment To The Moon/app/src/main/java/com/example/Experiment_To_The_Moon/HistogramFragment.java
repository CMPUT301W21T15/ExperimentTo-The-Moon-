/**
 * Classname: HistogramFragment
 * Date: April 2021
 */
package com.example.Experiment_To_The_Moon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collections;

public class HistogramFragment extends DialogFragment {
    private Statistics stats;
    private BarGraphSeries<DataPoint> series;
    private DocumentReference docRef;
    private String expType;
    private int num;

    public HistogramFragment(Statistics statistics){
        stats = statistics;
        docRef = statistics.getDocRef();
        expType = statistics.getExpType();
        num = statistics.getNumTrials();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.histogram_fragment_layout, null);
        drawHistogram(view);

        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Histogram")
                .setNegativeButton("Close", null)
                .create();
    }


    public void drawHistogram(View view){
        docRef.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("SUCCESSFUL_TASK", "the task is successful");
                    if (task.getResult().isEmpty()) {
                        return;
                    }
                    GraphView histogram = (GraphView) view.findViewById(R.id.histogram);
                    series = new BarGraphSeries<DataPoint>();
                    int n = 0;
                    int max_count = 0;
                    if(expType.equals("Count") || expType.equals("NonNegInt")) {
                        ArrayList<Long> values = new ArrayList<Long>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            long val = (Long) doc.getData().get("data");
                            values.add(val);
                            n = n + 1;
                        }
                        Collections.sort(values);
                        int count_in_bin = 0;
                        int bin = 1;
                        int div = (int) Math.ceil((double) num/5);
                        for(int i = 0; i < n; i++){
                            if(values.get(i) >= div){
                                series.appendData(new DataPoint(div, count_in_bin), true, 100);
                                if(count_in_bin > max_count){
                                    max_count = count_in_bin;
                                }
                                bin += 1;
                                div += div;
                                count_in_bin = 0;
                            }
                            count_in_bin += 1;
                        }
                    } else if (expType.equals("Measurement")){
                        ArrayList<Float> values = new ArrayList<Float>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            float val = (Float) doc.getData().get("data");
                            values.add(val);
                            n = n + 1;
                        }
                        Collections.sort(values);
                        int count_in_bin = 0;
                        int bin = 1;
                        int div = (int) Math.ceil((double) num/5);
                        for(int i = 0; i < n; i++){
                            if(values.get(i) >= div){
                                series.appendData(new DataPoint(div, count_in_bin), true, 100);
                                if(count_in_bin > max_count){
                                    max_count = count_in_bin;
                                }
                                bin += 1;
                                div += div;
                                count_in_bin = 0;
                            }
                            count_in_bin += 1;
                        }
                    } else if (expType.equals("Binomial")) {
                        int ones = 0;
                        int zeros = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Boolean boolVal = (Boolean) doc.getData().get("data");
                            if (boolVal) {
                                ones += 1;
                            } else {
                                zeros += 1;
                            }
                        }
                        series.appendData(new DataPoint(0, zeros), true, 100);
                        series.appendData(new DataPoint(1, ones), true, 100);
                    }
                    histogram.addSeries(series);
                    histogram.getViewport().setYAxisBoundsManual(true);
                    histogram.getViewport().setMinY(0);
                    histogram.getViewport().setMaxY(max_count+1);

                    // activate zooming and scrolling
                    histogram.getViewport().setScalable(true);

                    histogram.getViewport().setScrollable(true);

                    histogram.getViewport().setScalableY(true);

                    histogram.getViewport().setScrollableY(true);

                    // styling
                    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                        }
                    });
                }
            }
        });
    }
}
