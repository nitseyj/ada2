package com.example.assm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ListingDetailActivity extends AppCompatActivity {

    TextView txtTitle, txtPrice, txtQuantity, txtSellerInfo, txtDesc, txtStatus;
    Button btnPurchase, btnBackDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);

        txtTitle = findViewById(R.id.txtTitleDetail);
        txtPrice = findViewById(R.id.txtPriceDetail);
        txtQuantity = findViewById(R.id.txtQuantityDetail);
        txtSellerInfo = findViewById(R.id.txtSellerInfoDetail);
        txtDesc = findViewById(R.id.txtDescDetail);
        txtStatus = findViewById(R.id.txtStatusDetail);
        btnPurchase = findViewById(R.id.btnPurchase);
        btnBackDetail = findViewById(R.id.btnBackDetail); // ← new

        btnBackDetail.setOnClickListener(v -> finish()); // ← new

        String title = getIntent().getStringExtra("title");
        String price = getIntent().getStringExtra("price");
        String quantity = getIntent().getStringExtra("quantity");
        String seller = getIntent().getStringExtra("seller");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        String description = getIntent().getStringExtra("description");
        boolean available = getIntent().getBooleanExtra("available", true);

        txtTitle.setText(title);
        txtPrice.setText("RM " + price);
        txtQuantity.setText(quantity + " kg");
        txtSellerInfo.setText("Seller: " + seller + "\nPhone: " + phone + "\nAddress: " + address);
        txtDesc.setText("Description:\n" + description);
        txtStatus.setText(available ? "Available" : "Sold");

        if (available) {
            btnPurchase.setOnClickListener(v -> {
                SQLiteHelper dbHelper = new SQLiteHelper(this);
                boolean success = dbHelper.purchaseListing(title);

                if (success) {
                    String buyerId = getIntent().getStringExtra("buyerId");
                    String now = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

                    dbHelper.insertPurchase(buyerId, title, price, description, seller, now);

                    Toast.makeText(this, "Purchase Confirmed!", Toast.LENGTH_SHORT).show();

                    Intent backToDashboard = new Intent(this, BuyerDashboardActivity.class);
                    backToDashboard.putExtra("id", buyerId);
                    backToDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(backToDashboard);
                    finish();

                } else {
                    Toast.makeText(this, "Item is unavailable or sold out.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btnPurchase.setEnabled(false);
            btnPurchase.setText("Unavailable");
            txtStatus.setText("Sold");
        }
    }
}
