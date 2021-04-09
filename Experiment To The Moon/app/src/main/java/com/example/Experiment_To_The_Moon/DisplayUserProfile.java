package com.example.Experiment_To_The_Moon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Objects;

public class DisplayUserProfile extends AppCompatActivity implements Serializable {

    private static final String TAG = "DisplayUserProfile";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User currentUser;
    private User lookupUser;
    private Intent switchActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_profile);


        switchActivityIntent = getIntent();
        currentUser = (User) switchActivityIntent.getSerializableExtra("currentUser"); // get currentUser
        lookupUser = (User) switchActivityIntent.getSerializableExtra("lookupUser"); // look at this user's profile

        FloatingActionButton profile_back = findViewById(R.id.user_profile_back);
        FloatingActionButton profile_update = findViewById(R.id.user_profile_update);
        TextView userIDTextView = findViewById(R.id.user_id);
        EditText contactInfoEditText = findViewById(R.id.contact_info);

        DocumentReference docRef = db.collection("Users").document(lookupUser);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.requireNonNull(document).exists()) {
                    // Write contents of database
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    String contactInfo = (String) Objects.requireNonNull(document.getData()).get("contactInfo");
                    userIDTextView.setText(lookupUser);
                    contactInfoEditText.setText(contactInfo);
                } else {
                    // If it does not exist, display error
                    Log.d(TAG, "No such document");
                    Toast toast = Toast.makeText(this,"UID does not exist", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast toast = Toast.makeText(this,"UID does not exist", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // if you are not the user, you cannot edit contact info
        if (lookupUser.compareTo(currentUser) == 0) {
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
            profileBack();
        });

        profile_update.setOnClickListener(view -> {
            final String newContactInfo = contactInfoEditText.getText().toString();
            if(newContactInfo.length()>0) { // We do not add anything if the field is empty.
                DocumentReference docRef2 = db.collection("Users").document(currentUser);
                docRef2
                        .update("contactInfo", newContactInfo)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Update successful");
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            }
        });
    }

    private void profileBack() {
        switchActivityIntent.putExtra("currentUser", currentUser);
        setResult(RESULT_OK, switchActivityIntent);
        finish(); // switch back to main activity
    }

    @Override
    public void onBackPressed() {
        profileBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            profileBack();
            return true;
        }
        return false;
    }
}