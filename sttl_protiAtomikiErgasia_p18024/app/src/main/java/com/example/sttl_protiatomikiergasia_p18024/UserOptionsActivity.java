package com.example.sttl_protiatomikiergasia_p18024;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class UserOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options);

        Button viewRoutes = findViewById(R.id.viewRoutes);
        Button startTracking = findViewById(R.id.startTrackingButton);
        Intent myIntent = getIntent();
        Customer currentCustomer = (Customer) myIntent.getSerializableExtra("LOGGED_USER");

        startTracking.setOnClickListener(v -> {
            Intent newIntent = new Intent(UserOptionsActivity.this,TrackRouteInProgress.class);
            newIntent.putExtra("LOGGED_USER", currentCustomer);
            startActivity(newIntent);
        });

        viewRoutes.setOnClickListener(v -> {
            Intent newIntent = new Intent(UserOptionsActivity.this,ShowAllRoutes.class);
            newIntent.putExtra("LOGGED_USER", currentCustomer);
            startActivity(newIntent);
        });
    }
}