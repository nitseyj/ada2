package com.example.assm2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etAddress, etAccountID, etPin;
    CheckBox cbTos;
    RadioGroup roleGroup;
    RadioButton rbBuyer, rbSeller;
    Button btnRegister, btnBack;
    SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new SQLiteHelper(this);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etAccountID = findViewById(R.id.etAccountID);
        etPin = findViewById(R.id.etPin);
        cbTos = findViewById(R.id.cbTos);
        roleGroup = findViewById(R.id.roleGroup);
        rbBuyer = findViewById(R.id.rbBuyer);
        rbSeller = findViewById(R.id.rbSeller);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBackRegister);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String id = etAccountID.getText().toString().trim();
            String pin = etPin.getText().toString().trim();
            String role = rbBuyer.isChecked() ? "buyer" : rbSeller.isChecked() ? "seller" : "";

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(address) || TextUtils.isEmpty(id) || TextUtils.isEmpty(pin)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(role)) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbTos.isChecked()) {
                Toast.makeText(this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pin.length() != 6) {
                Toast.makeText(this, "PIN must be 6 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertUser(id, name, phone, email, address, pin, role);

            if (success) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                dbHelper.logAllUsers();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Account ID already exists", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }
}
