package com.example.Experiment_To_The_Moon;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

// fragment for generating QR codes for a specific type of trial in a specific experiment.
public class GenerateQRFragment extends DialogFragment {

    public String type; // type of the experiment
    public String name; // name of the experiment

    public Spinner QR_Spinner;
    public Button generateQRButton;
    public Button saveQRButton;
    public ImageView QR_Code;

    public GenerateQRFragment(String type, String new_name) {
        this.type = type;
        this.name = new_name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.generate_qr_fragment, null);

        QR_Code = (ImageView) view.findViewById(R.id.generate_qr_imgview);

        QR_Spinner = (Spinner) view.findViewById(R.id.generate_qr_spinner);
        populateSpinner();

        generateQRButton = view.findViewById(R.id.generate_qr_code_button);
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String spinner_item = QR_Spinner.getSelectedItem().toString();

                /* The text that the QR code will hold is of the form:
                   "experiment_name,experiment_type,trial_result"
                   Later, when the QR code is scanned, these values will be used to
                   create a new trial with the desired information as the outcome.
                 */
                String QR_text = name + "," + type + "," + spinner_item;

                // weird library stuff.
                // just sets up the QR code.
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(QR_text, BarcodeFormat.QR_CODE, 500, 500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    QR_Code.setImageBitmap(bitmap); // show the QR code on the screen.
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        saveQRButton = view.findViewById(R.id.generate_qr_save_button);
        saveQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
            }
        });

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder
        .setView(view)
        .setTitle("View Trials")
        .setNegativeButton("Cancel", null)
        .setPositiveButton("Ok", null)
        .create();
    }

    // populate the spinner based on the type of experiment.
    // e.g. you can't have a "pass" result in an experiment of type Count, so we don't want the "pass" option to be shown.
    public void populateSpinner() {
        if (type.equals("Binomial")) {
            ArrayAdapter<String> QRTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_boolean_types));
            QRTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            QR_Spinner.setAdapter(QRTypeAdapter);
        } else if (type.equals("Count")) {
            ArrayAdapter<String> QRTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_qr_types_count));
            QRTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            QR_Spinner.setAdapter(QRTypeAdapter);
        } else if (type.equals("NonNegInt")) {
            ArrayAdapter<String> QRTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_qr_types_NNI));
            QRTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            QR_Spinner.setAdapter(QRTypeAdapter);
        }
    }

    public void saveToGallery() {
        /*
        BitmapDrawable bitmapDrawable = (BitmapDrawable) QR_Code.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();


        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/ExperimentToTheMoon");
        dir.mkdirs();

        String filename = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, filename);

        try {
            outputStream = new FileOutputStream(outFile);
        } catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        try {
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

         */
    }
}




