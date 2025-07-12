package com.example.assm2;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateListingActivity extends AppCompatActivity {

    EditText etTitle, etQuantity, etPrice, etDesc;
    Spinner spinnerCategory;
    TextView txtAddress;
    Button btnUpload, btnBack;

    SQLiteHelper dbHelper;
    String sellerId, sellerName, sellerPhone, sellerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        dbHelper = new SQLiteHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        txtAddress = findViewById(R.id.txtAddress);
        btnUpload = findViewById(R.id.btnUploadListing);
        btnBack = findViewById(R.id.btnBackCreate);

        String[] categories = {"Plastic", "Glass", "Paper", "Aluminum", "Electronics", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // Get seller ID passed via intent
        sellerId = getIntent().getStringExtra("id");
        if (sellerId == null) {
            Toast.makeText(this, "Missing seller ID!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Get seller info from DB
        sellerName = "";
        sellerPhone = "";
        sellerAddress = "";

        Cursor cursor = dbHelper.getUserById(sellerId);
        if (cursor != null && cursor.moveToFirst()) {
            sellerName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            sellerPhone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            sellerAddress = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            txtAddress.setText("Location: " + sellerAddress);
            cursor.close();
        } else {
            Toast.makeText(this, "Unable to fetch seller details", Toast.LENGTH_SHORT).show();
        }

        // Back button logic
        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(this, SellerDashboardActivity.class);
            backIntent.putExtra("id", sellerId);
            startActivity(backIntent);
            finish();
        });

        // Upload listing logic
        btnUpload.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(priceStr)
                    || TextUtils.isEmpty(qtyStr) || TextUtils.isEmpty(desc)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Upload")
                    .setMessage("Are you sure you want to upload this listing?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            String listingId = "LST" + System.currentTimeMillis();
                            double price = Double.parseDouble(priceStr);
                            int quantity = Integer.parseInt(qtyStr);

                            boolean inserted = dbHelper.insertListing(
                                    listingId, title, desc, category, price,
                                    quantity, sellerId, sellerName, sellerPhone, sellerAddress);

                            if (inserted) {
                                Toast.makeText(this, "Listing uploaded!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Upload failed.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
