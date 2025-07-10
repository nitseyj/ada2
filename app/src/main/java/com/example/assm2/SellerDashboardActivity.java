package com.example.assm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SellerDashboardActivity extends AppCompatActivity {

    TextView txtWelcomeSeller, txtRevenue, txtItemsSold;
    Button btnCreateListing, btnLogout;
    ListView listView;

    // Bottom nav icons
    ImageView navRecycle, navMap, navPricing, navContact, navProfile;

    SQLiteHelper dbHelper;
    String sellerId, sellerName;

    ArrayList<ListingItem> sellerListings = new ArrayList<>();
    ListingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        dbHelper = new SQLiteHelper(this);
        sellerId = getIntent().getStringExtra("id");
        sellerName = "Seller";

        Cursor cursor = dbHelper.getUserById(sellerId);
        if (cursor != null && cursor.moveToFirst()) {
            sellerName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
        }

        txtWelcomeSeller = findViewById(R.id.txtWelcomeSeller);
        txtRevenue = findViewById(R.id.txtRevenue);
        txtItemsSold = findViewById(R.id.txtItemsSold);
        btnCreateListing = findViewById(R.id.btnCreateListing);
        btnLogout = findViewById(R.id.btnLogoutSeller);
        listView = findViewById(R.id.listViewSeller);

        navRecycle = findViewById(R.id.navRecycle);
        navMap = findViewById(R.id.navMap);
        navPricing = findViewById(R.id.navPricing);
        navContact = findViewById(R.id.navContact);
        navProfile = findViewById(R.id.navProfile);

        txtWelcomeSeller.setText("Welcome, " + sellerName);

        loadSellerListings();

        // Create new listing
        btnCreateListing.setOnClickListener(v -> {
            Intent listingIntent = new Intent(this, CreateListingActivity.class);
            listingIntent.putExtra("id", sellerId);
            startActivity(listingIntent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(this, MainActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
        });

        // Delete on tap
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ListingItem item = sellerListings.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Delete Listing")
                    .setMessage("Are you sure you want to delete \"" + item.title + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dbHelper.deleteListingById(item.title);
                        loadSellerListings();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Bottom Navigation
        navRecycle.setOnClickListener(v -> recreate());

        navMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("id", sellerId);
            startActivity(intent);
        });

        navPricing.setOnClickListener(v -> {
            Intent intent = new Intent(this, PricingActivity.class);
            intent.putExtra("id", sellerId);
            startActivity(intent);
        });

        navContact.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            intent.putExtra("id", sellerId);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", sellerId);
            startActivity(intent);
        });
    }

    private void loadSellerListings() {
        sellerListings.clear();
        double totalRevenue = 0;
        int totalQty = 0;

        Cursor cursor = dbHelper.getListingsBySellerId(sellerId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
                String seller = cursor.getString(cursor.getColumnIndexOrThrow("seller_name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("seller_phone"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("seller_address"));
                int qty = Integer.parseInt(quantity);
                double rev = Double.parseDouble(price) * qty;
                boolean available = qty > 0;

                totalRevenue += rev;
                totalQty += qty;

                sellerListings.add(new ListingItem(title, price, quantity, seller, available, description, phone, address));
            }
            cursor.close();
        }

        txtRevenue.setText("Total Revenue: RM" + String.format("%.2f", totalRevenue));
        txtItemsSold.setText("Total Items Sold: " + totalQty + " kg");

        adapter = new ListingAdapter(this, sellerListings);
        listView.setAdapter(adapter);
    }
}
