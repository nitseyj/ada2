package com.example.assm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateListingActivity extends AppCompatActivity {

    Button btnBackToSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        btnBackToSeller = findViewById(R.id.btnBackToSeller);

        btnBackToSeller.setOnClickListener(v -> {
            startActivity(new Intent(CreateListingActivity.this, SellerDashboardActivity.class));
            finish();
        });

        Toast.makeText(this, "Create Listing Page (To Be Developed)", Toast.LENGTH_SHORT).show();
    }
}
