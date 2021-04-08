package com.example.Experiment_To_The_Moon;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

// fragment for generating QR codes for a specific type of trial in a specific experiment.
public class GenerateQRFragment extends DialogFragment {

    public String type; // type of the experiment
    public String name; // name of the experiment
    public String uid; // current application user
    public boolean qr_shown; // true if there is a QR code shown on screen, false otherwise.

    public Spinner QR_Spinner;
    public EditText editText;
    public Button generateQRButton;
    public Button registerBarCodeButton;
    public Button saveQRButton;
    public ImageView QR_Code;
    private FirebaseFirestore db;

    public GenerateQRFragment() { }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.generate_qr_fragment, null);

        // input parameters. name of experiment, and type of experiment.
        name = getArguments().getString("name");
        type = getArguments().getString("type");
        uid = getArguments().getString("uid");
        qr_shown = false;

        QR_Code = (ImageView) view.findViewById(R.id.generate_qr_imgview);
        QR_Spinner = (Spinner) view.findViewById(R.id.generate_qr_spinner);
        editText = view.findViewById(R.id.generate_qr_edit_text);

        /* If the experiment is NonNegInt, the user can type a number as the input,
           otherwise, they use the spinner to select an option
         */
        if (type.equals("NonNegInt")) {
            QR_Spinner.setVisibility(view.INVISIBLE);
            editText.setVisibility(view.VISIBLE);
        } else {
            QR_Spinner.setVisibility(view.VISIBLE);
            editText.setVisibility(view.INVISIBLE);
            populateSpinner(); // set up the spinner
        }

        generateQRButton = view.findViewById(R.id.generate_qr_code_button);
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQR(); // generate the QR code.
                qr_shown = true;
            }
        });

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        saveQRButton = view.findViewById(R.id.generate_qr_save_button);
        saveQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qr_shown) {
                    saveToFiles();
                } else {
                    Toast.makeText(getContext(), "Please generate a QR code you wish to save.", Toast.LENGTH_LONG).show();
                }
            }
        });

        registerBarCodeButton = view.findViewById(R.id.generate_qr_register_bar_code);
        registerBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBarCode();
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
        }
    }

    /* When the user generates a QR code, they can then click the Save button, and an image of the QR code
       will be saved into "Files" on the device.
       The name of the QR image in Files will be: "(name of experiment), Result (result)"
     */
    public void saveToFiles() {
        String input;
        if (type.equals("NonNegInt")) {
            input = editText.getText().toString();
        } else {
            input = QR_Spinner.getSelectedItem().toString();
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) QR_Code.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        MediaStore.Images.Media.insertImage(
                getActivity().getApplicationContext().getContentResolver(),
                bitmap,
                name + ", Result " + input,
                ""
        );
    }

    public void showQR() {
        String input;
        if (type.equals("NonNegInt")) {
            input = editText.getText().toString();
            if (input.equals("")) {
                Toast.makeText(getContext(), "Please enter a value for your trial.", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            input = QR_Spinner.getSelectedItem().toString();
        }


        /* The text that the QR code will hold is of the form:
           "experiment_name,experiment_type,trial_result"
           Later, when the QR code is scanned, these values will be used to
           create a new trial with the desired information as the outcome.
         */
        String QR_text = name + "," + type + "," + input;

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


    public void registerBarCode() {
        String input;
        if (type.equals("NonNegInt")) {
            input = editText.getText().toString();
            if (input.equals("")) {
                Toast.makeText(getContext(), "Please enter a value for your trial.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(GenerateQRFragment.this);

        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan Bar Code");
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);

        integrator.initiateScan();
    }

    // factory method for creating GenerateQRFragment(s). Used for passing in name and type.
    public static GenerateQRFragment newInstance(String name, String type, String uid) {
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("type", type);
        args.putString("uid", uid);
        GenerateQRFragment f = new GenerateQRFragment();
        f.setArguments(args);
        return f;
    }

    /*
        If the user scans a barcode to register it, the barcode is added to the db with its desired result.
        Then, if the user scans that same bar code from the home screen, they will add a new trial to the experiment
        with the same result.
        Note: if the user scans a barcode that has already been registered, the old one is overwritten with the new result.
        Note: The same barcode can be registered for multiple experiments. The scope of a specific bar code is only the experiment
        that it is registered in.
     */
    public void registerBarCodeInDataBase(String bar_code) {
        String input;
        if (type.equals("NonNegInt")) {
            input = editText.getText().toString();
        } else {
            input = QR_Spinner.getSelectedItem().toString();
        }

        // get database reference.
        db = FirebaseFirestore.getInstance();
        final CollectionReference experimentsCollection = db
                .collection("Users")
                .document(uid)
                .collection("Barcodes");

        // add the barcode result, experiment name, and experiment type to hashmap.
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("type", type);
        data.put("result", input);

        // add the data to Firebase.
        // Located in Users->uid->Barcodes.
        experimentsCollection
                .document(bar_code)
                .set(data, SetOptions.merge());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // this is the result after scanning a barcode.
        if (requestCode == 49374) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            registerBarCodeInDataBase(result.getContents());
        }
    }
}
