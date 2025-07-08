package com.example.assm2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BuyerDashboardActivity extends AppCompatActivity {

    ListView listView;
    Button btnLogout;
    SQLiteHelper dbHelper;
    String buyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_buyer_dashboard);
            Log.d("BUYER", "Layout loaded successfully");

            listView = findViewById(R.id.listViewBuyer);
            btnLogout = findViewById(R.id.btnLogoutBuyer);
            dbHelper = new SQLiteHelper(this);

            buyerId = getIntent().getStringExtra("id");
            Log.d("BUYER", "Received buyerId: " + buyerId);

            if (buyerId == null || buyerId.isEmpty()) {
                Toast.makeText(this, "Buyer ID not found!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            btnLogout.setOnClickListener(v -> showLogoutDialog());

            loadAllListings();

        } catch (Exception e) {
            Log.e("BUYER_CRASH", "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadAllListings() {
        try {
            Cursor cursor = dbHelper.getAllListings();
            ArrayList<String> listings = new ArrayList<>();

            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                String seller = cursor.getString(cursor.getColumnIndexOrThrow("seller_name"));
                listings.add(title + " - RM" + price + " (Seller: " + seller + ")");
            }

            cursor.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, listings);
            listView.setAdapter(adapter);

        } catch (Exception ex) {
            Log.e("BUYER_LOAD", "Error loading listings: " + ex.getMessage(), ex);
            Toast.makeText(this, "Error loading listings: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
