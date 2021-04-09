package com.example.Experiment_To_The_Moon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

public class DisplayUserProfile extends AppCompatActivity implements Serializable {

    private static final String TAG = "DisplayUserProfile";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_profile);

        Intent switchActivityIntent = getIntent();
        User currentUser = (User) switchActivityIntent.getSerializableExtra("currentUser"); // get currentUser
        User lookupUser = (User) switchActivityIntent.getSerializableExtra("lookupUser"); // look at this user's profile

        FloatingActionButton profile_back = findViewById(R.id.user_profile_back);
        FloatingActionButton profile_update = findViewById(R.id.user_profile_update);
        TextView userIDTextView = findViewById(R.id.user_id);
        EditText contactInfoEditText = findViewById(R.id.contact_info);

        userIDTextView.setText(lookupUser.getUid());
        contactInfoEditText.setText(lookupUser.getContactInfo());

        // if you are not the user, you cannot edit contact info
        if (lookupUser.getUid().compareTo(currentUser.getUid()) == 0) {
            contactInfoEditText.setEnabled(true);
            contactInfoEditText.setClickable(true);
            profile_update.setEnabled(true);
            profile_update.setClickable(true);
        } else {
            contactInfoEditText.setEnabled(false);
            contactInfoEditText.setClickable(false);
            profile_update.setEnabled(false);
            profile_update.setClickable(false);
        }

        profile_back.setOnClickListener(view -> {
            switchActivityIntent.putExtra("currentUser", currentUser);
            setResult(RESULT_OK, switchActivityIntent);
            finish(); // switch back to main activity
        });

        profile_update.setOnClickListener(view -> {
            final String newContactInfo = contactInfoEditText.getText().toString();
            if(newContactInfo.length()>0) { // We do not add anything if the field is empty.
                DocumentReference docRef = db.collection("Users").document(currentUser.getUid());
                docRef
                        .update("contactInfo", newContactInfo)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Update successful");
                            currentUser.setContactInfo(newContactInfo);
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            }
        });
    }
}