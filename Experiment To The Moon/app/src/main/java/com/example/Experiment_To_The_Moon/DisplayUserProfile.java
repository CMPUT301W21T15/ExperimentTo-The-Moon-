package com.example.Experiment_To_The_Moon;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

public class DisplayUserProfile extends AppCompatActivity {

    User currentUser;
    FloatingActionButton profile_back = findViewById(R.id.user_profile_back);
    FloatingActionButton profile_update = findViewById(R.id.user_profile_update);
    TextView userID = findViewById(R.id.user_id);
    TextView contactInfo = findViewById(R.id.contact_info);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_profile);

        profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}