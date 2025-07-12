package com.example.assm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BuyerDashboardActivity extends AppCompatActivity {

    TextView txtWelcomeBuyer;
    EditText etSearch;
    Button btnSortPrice, btnSortQuantity, btnLogoutBuyer, btnViewHistory;
    ListView listView;
    LinearLayout layoutSuggestions;

    // Bottom nav icons
    ImageView navRecycle, navMap, navPricing, navContact, navProfile;

    SQLiteHelper dbHelper;
    String buyerId;
    String buyerName;

    ArrayList<ListingItem> allListings = new ArrayList<>();
    ArrayList<ListingItem> displayedListings = new ArrayList<>();
    ListingAdapter adapter;

    String[] suggestions = {"Plastic", "Glass", "Paper", "Aluminum", "Electronics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        dbHelper = new SQLiteHelper(this);

        buyerId = getIntent().getStringExtra("id");
        buyerName = "Buyer";

        Cursor cursor = dbHelper.getUserById(buyerId);
        if (cursor != null && cursor.moveToFirst()) {
            buyerName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
        }

        txtWelcomeBuyer = findViewById(R.id.txtWelcomeBuyer);
        etSearch = findViewById(R.id.etSearch);
        btnSortPrice = findViewById(R.id.btnSortPrice);
        btnSortQuantity = findViewById(R.id.btnSortQuantity);
        btnLogoutBuyer = findViewById(R.id.btnLogoutBuyer);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        listView = findViewById(R.id.listViewBuyer);
        layoutSuggestions = findViewById(R.id.layoutSuggestions);

        // Bottom navigation buttons
        navRecycle = findViewById(R.id.navRecycle);
        navMap = findViewById(R.id.navMap);
        navPricing = findViewById(R.id.navPricing);
        navContact = findViewById(R.id.navContact);
        navProfile = findViewById(R.id.navProfile);

        txtWelcomeBuyer.setText("Welcome, " + buyerName);

        loadSuggestions();
        loadListings();

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            filterListings(keyword);
            return true;
        });

        btnSortPrice.setOnClickListener(v -> sortBy("price"));
        btnSortQuantity.setOnClickListener(v -> sortBy("quantity"));

        btnLogoutBuyer.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, PurchaseHistoryActivity.class);
            intent.putExtra("id", buyerId);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ListingItem selected = displayedListings.get(position);
            Intent intent = new Intent(this, ListingDetailActivity.class);
            intent.putExtra("title", selected.title);
            intent.putExtra("price", selected.price);
            intent.putExtra("quantity", selected.quantity);
            intent.putExtra("seller", selected.seller);
            intent.putExtra("description", selected.description);
            intent.putExtra("phone", selected.phone);
            intent.putExtra("address", selected.address);
            intent.putExtra("available", selected.available);
            intent.putExtra("buyerId", buyerId);
            startActivity(intent);
        });

        // Bottom nav actions
        navRecycle.setOnClickListener(v -> recreate());

        navMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("id", buyerId);
            startActivity(intent);
        });

        navPricing.setOnClickListener(v -> {
            Intent intent = new Intent(this, PricingActivity.class);
            intent.putExtra("id", buyerId);
            startActivity(intent);
        });

        navContact.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            intent.putExtra("id", buyerId);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", buyerId);
            startActivity(intent);
        });
    }

    private void loadSuggestions() {
        layoutSuggestions.removeAllViews();

        for (String item : suggestions) {
            Button btn = new Button(this);
            btn.setText(item);
            btn.setOnClickListener(v -> {
                etSearch.setText(item);
                filterListings(item);
            });
            layoutSuggestions.addView(btn);
        }
    }

    private void loadListings() {
        Cursor cursor = dbHelper.getAllListings();
        allListings.clear();

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
            String sellerName = cursor.getString(cursor.getColumnIndexOrThrow("seller_name"));
            String sellerPhone = cursor.getString(cursor.getColumnIndexOrThrow("seller_phone"));
            String sellerAddress = cursor.getString(cursor.getColumnIndexOrThrow("seller_address"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

            boolean available = Integer.parseInt(quantity) > 0;

            ListingItem item = new ListingItem(title, price, quantity, sellerName, available, description, sellerPhone, sellerAddress);
            allListings.add(item);
        }
        cursor.close();

        displayedListings = new ArrayList<>(allListings);
        adapter = new ListingAdapter(this, displayedListings);
        listView.setAdapter(adapter);
    }

    private void filterListings(String keyword) {
        displayedListings.clear();

        for (ListingItem item : allListings) {
            if (item.title.toLowerCase().contains(keyword.toLowerCase())) {
                displayedListings.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void sortBy(String type) {
        if (type.equals("price")) {
            displayedListings.sort((a, b) -> Double.compare(Double.parseDouble(a.price), Double.parseDouble(b.price)));
        } else if (type.equals("quantity")) {
            displayedListings.sort((a, b) -> Integer.compare(Integer.parseInt(a.quantity), Integer.parseInt(b.quantity)));
        }

        adapter.notifyDataSetChanged();
    }
}
