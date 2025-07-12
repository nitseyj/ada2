package com.example.assm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnBuyer, btnSeller;
    TextView txtFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBuyer = findViewById(R.id.btnBuyer);
        btnSeller = findViewById(R.id.btnSeller);
        txtFirstTime = findViewById(R.id.txtFirstTime);

        btnBuyer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("role", "buyer");
            startActivity(intent);
        });

        btnSeller.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("role", "seller");
            startActivity(intent);
        });

        txtFirstTime.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.putExtra("role", "buyer");
            startActivity(intent);
        });
    }
}                                                       