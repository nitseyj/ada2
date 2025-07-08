package com.example.assm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    Button btnBackToSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnBackToSeller = findViewById(R.id.btnBackToSeller);

        btnBackToSeller.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SellerDashboardActivity.class));
            finish();
        });
    }
}
