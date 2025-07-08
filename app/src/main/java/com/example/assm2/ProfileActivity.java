package com.example.assm2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etPhone, etAddress, etPin;
    Button btnSave, btnBack;
    CheckBox checkboxShowPIN;

    SQLiteHelper dbHelper;
    String userId, userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etProfileName);
        etPhone = findViewById(R.id.etProfilePhone);
        etAddress = findViewById(R.id.etProfileAddress);
        etPin = findViewById(R.id.etProfilePIN);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBackProfile);
        checkboxShowPIN = findViewById(R.id.checkboxShowPIN); // ✅ NEW

        dbHelper = new SQLiteHelper(this);
        userId = getIntent().getStringExtra("id");

        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            etPin.setText(cursor.getString(cursor.getColumnIndexOrThrow("pin")));
            userRole = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
        }

        // ✅ Toggle show/hide PIN
        checkboxShowPIN.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPin.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                etPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
            etPin.setSelection(etPin.getText().length()); // keep cursor at the end
        });

        btnSave.setOnClickListener(v -> saveProfileChanges());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(pin)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        values.put("address", address);
        values.put("pin", pin);

        int rows = dbHelper.getWritableDatabase().update("users", values, "id=?", new String[]{userId});

        if (rows > 0) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();

            // ✅ Auto-redirect based on role
            Intent intent;
            if ("buyer".equalsIgnoreCase(userRole)) {
                intent = new Intent(this, BuyerDashboardActivity.class);
            } else {
                intent = new Intent(this, SellerDashboardActivity.class);
            }

            intent.putExtra("id", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
