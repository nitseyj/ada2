package com.example.assm2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText etName, etPhone, etAddress, etPin;
    Button btnSave, btnBack, btnUploadImage;
    ImageView imgProfile;
    CheckBox checkboxShowPIN;

    SQLiteHelper dbHelper;
    String userId, userRole;
    Bitmap selectedBitmap = null;

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
        checkboxShowPIN = findViewById(R.id.checkboxShowPIN);
        imgProfile = findViewById(R.id.imgProfile);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        dbHelper = new SQLiteHelper(this);
        userId = getIntent().getStringExtra("id");

        // Load user profile
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            etPin.setText(cursor.getString(cursor.getColumnIndexOrThrow("pin")));
            userRole = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            cursor.close();
        }

        // Load profile picture
        byte[] imageBytes = dbHelper.getProfilePicture(userId);
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgProfile.setImageBitmap(bitmap);
        }

        // Toggle PIN visibility
        checkboxShowPIN.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPin.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                etPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
            etPin.setSelection(etPin.getText().length());
        });

        // Upload image
        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
        });

        // Save changes
        btnSave.setOnClickListener(v -> saveProfileChanges());

        // Back
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProfile.setImageBitmap(selectedBitmap);

                // Save image to SQLite
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();

                boolean saved = dbHelper.updateProfilePicture(userId, imageBytes);
                if (!saved) {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image load failed", Toast.LENGTH_SHORT).show();
            }
        }
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

            // Redirect
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
