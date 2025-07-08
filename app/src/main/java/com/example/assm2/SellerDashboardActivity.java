package com.example.assm2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SellerDashboardActivity extends AppCompatActivity {

    Button btnCreate, btnLogout;
    ListView listView;
    SQLiteHelper dbHelper;

    String sellerId, sellerName, sellerPhone, sellerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        btnCreate = findViewById(R.id.btnCreateListing);
        btnLogout = findViewById(R.id.btnLogoutSeller);
        listView = findViewById(R.id.listViewListings);
        dbHelper = new SQLiteHelper(this);

        // Get seller info from intent
        Intent intent = getIntent();
        sellerId = intent.getStringExtra("id");
        sellerName = intent.getStringExtra("name");
        sellerPhone = intent.getStringExtra("phone");
        sellerAddress = intent.getStringExtra("address");

        // Open CreateListingActivity
        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateListingActivity.class);
            i.putExtra("seller_id", sellerId);
            i.putExtra("seller_name", sellerName);
            i.putExtra("seller_phone", sellerPhone);
            i.putExtra("seller_address", sellerAddress);
            startActivity(i);
        });

        // Logout confirmation
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Load listings on startup
        loadListings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListings(); // Refresh listings when returning
    }

    private void loadListings() {
        Cursor cursor = dbHelper.getListingsBySellerId(sellerId);
        ArrayList<String> listings = new ArrayList<>();

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
            listings.add(title + " - RM" + price + " (" + quantity + " pcs)");
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listings);
        listView.setAdapter(adapter);
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
