package com.example.assm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etID, etPIN;
    Button btnLogin, btnBack;
    SQLiteHelper dbHelper;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        role = getIntent().getStringExtra("role");
        dbHelper = new SQLiteHelper(this);

        etID = findViewById(R.id.etID);
        etPIN = findViewById(R.id.etPIN);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBackLogin);

        btnLogin.setOnClickListener(v -> {
            String id = etID.getText().toString().trim();
            String pin = etPIN.getText().toString().trim();

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pin)) {
                Toast.makeText(this, "Please enter ID and PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.validateLogin(id, pin, role)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                if ("seller".equals(role)) {
                    // Fetch seller info from DB
                    Cursor cursor = dbHelper.getUserById(id);
                    if (cursor != null && cursor.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                        String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                        cursor.close();

                        Intent intent = new Intent(this, SellerDashboardActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("name", name);
                        intent.putExtra("phone", phone);
                        intent.putExtra("address", address);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to load seller info", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Buyer login success — redirect to buyer dashboard
                    Intent intent = new Intent(this, BuyerDashboardActivity.class);
                    intent.putExtra("id", id); // Optional
                    startActivity(intent);
                    finish();
                }

            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
    }
}
