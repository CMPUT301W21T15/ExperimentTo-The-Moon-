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
/**
 * This class is a fragment that displays a histogram for an experiment's statistics
 */
public class HistogramFragment extends DialogFragment {
    private Statistics stats;
    private BarGraphSeries<DataPoint> series;
    private DocumentReference docRef;
    private String expType;
    private int num;
    private GraphView histogram;

    /**
     *
     * @param statistics
     * statistics objects with trial data
     */
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
        histogram = (GraphView) view.findViewById(R.id.histogram);
        series = new BarGraphSeries<DataPoint>();
        drawHistogram(view);

        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Histogram")
                .setNegativeButton("Close", null)
                .create();
    }

    /**
     * Used in calculating bin sizes
     * @param x
     * @return
     */
    public int roundTo50(float x){
        x = (float) Math.ceil(x);
        if(x%50==0){
            return (int) x;
        }
        return (int) (x + (50 - (x % 50)));
    }

    /**
     * display histogram
     * @param view
     * histogram fragment layout
     */
    public void drawHistogram(View view){
        docRef.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("SUCCESSFUL_TASK", "the task is successful");
                    if (task.getResult().isEmpty()) {
                        return;
                    }
                    int n = 0;
                    int max_count = 0;
                    int count_in_bin = 0;
                    float max = stats.max;
                    float min = stats.min;
                    float bin_size;
                    if(max <= 20){
                        bin_size = 5;
                    } else if(max <= 50){
                        bin_size = 10;
                    }else{
                        bin_size = roundTo50(max);
                        bin_size = bin_size/5;
                    }
                    float div = bin_size;
                    if(expType.equals("Count") || expType.equals("NonNegInt")) {
                        series.setDataWidth(bin_size);
                        histogram.getViewport().setYAxisBoundsManual(true);
                        histogram.getViewport().setMinY(0);
                        histogram.getViewport().setXAxisBoundsManual(true);
                        histogram.getViewport().setMinX(min - (min % bin_size));
                        histogram.getViewport().setMaxX(roundTo50(max));
                        if(max <= 20){
                            histogram.getViewport().setMaxX(25);
                        }
                        ArrayList<Long> values = new ArrayList<Long>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            long val = (Long) doc.getData().get("data");
                            values.add(val);
                            n = n + 1;
                        }
                        Collections.sort(values);
                        for(int i = 0; i < n; i++){
                            if(values.get(i) < div){
                                count_in_bin += 1;
                                if(i == n - 1){
                                    if(count_in_bin > max_count){
                                        max_count = count_in_bin;
                                    }
                                    series.appendData(new DataPoint(div - (bin_size/2), count_in_bin), true, 100);
                                }
                            }else{
                                if(count_in_bin > max_count){
                                    max_count = count_in_bin;
                                }
                                if(count_in_bin == 0){
                                    while(values.get(i) > div){
                                        div += bin_size;
                                    }
                                    count_in_bin += 1;
                                    if(i == n - 1){
                                        series.appendData(new DataPoint(div - (bin_size/2), count_in_bin), true, 100);
                                    }
                                }else{
                                    series.appendData(new DataPoint(div - (bin_size/2), count_in_bin), true, 100);
                                    div += bin_size;
                                    count_in_bin = 1;
                                }
                            }
                        }
                        histogram.getViewport().setMaxY(Math.ceil(max_count * 1.2));

                    } else if (expType.equals("Measurement")){
                        series.setDataWidth(bin_size);
                        histogram.getViewport().setYAxisBoundsManual(true);
                        histogram.getViewport().setMinY(0);
                        histogram.getViewport().setXAxisBoundsManual(true);
                        histogram.getViewport().setMinX(min - (min % bin_size));
                        histogram.getViewport().setMaxX(roundTo50(max));
                        ArrayList<Float> values = new ArrayList<Float>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            float val = (Float) doc.getData().get("data");
                            values.add(val);
                            n = n + 1;
                        }
                        Collections.sort(values);
                        for(int i = 0; i < n; i++){
                            if(values.get(i) < div){
                                count_in_bin += 1;
                                if(i == n - 1){
                                    if(count_in_bin > max_count){
                                        max_count = count_in_bin;
                                    }
                                    series.appendData(new DataPoint(div - (bin_size/2), count_in_bin), true, 100);
                                }
                            }else{
                                if(count_in_bin > max_count){
                                    max_count = count_in_bin;
                                }
                                if(count_in_bin == 0){
                                    while(values.get(i) > div){
                                        div += bin_size;
                                    }
                                    count_in_bin += 1;
                                    if(i == n - 1){
                                        series.appendData(new DataPoint(div - (bin_size/2), count_in_bin), true, 100);
                                    }
                                }else{
                                    series.appendData(new DataPoint(div - (bin_size/2), count_in_bin), true, 100);
                                    div += bin_size;
                                    count_in_bin = 1;
                                }
                            }
                        }
                        histogram.getViewport().setMaxY(Math.ceil(max_count * 1.2));
                    } else if (expType.equals("Binomial")) {
                        int ones = 0;
                        int zeros = 0;
                        int total = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Boolean boolVal = (Boolean) doc.getData().get("data");
                            if (boolVal) {
                                ones += 1;
                            } else {
                                zeros += 1;
                            }
                            total += 1;
                        }
                        series.appendData(new DataPoint(0 + 0.5, zeros), true, 100);
                        series.appendData(new DataPoint(1 + 0.5, ones), true, 100);
                        series.setDataWidth(1);
                        histogram.getViewport().setYAxisBoundsManual(true);
                        histogram.getViewport().setMinY(0);
                        histogram.getViewport().setMaxY(total + 1);
                        histogram.getViewport().setXAxisBoundsManual(true);
                        histogram.getViewport().setMaxX(2);
                    }
                    series.setSpacing(0);
                    series.setDrawValuesOnTop(true);
                    histogram.addSeries(series);


                    // set the bars to different colors
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
