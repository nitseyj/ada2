package com.example.assm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PurchaseHistoryActivity extends AppCompatActivity {

    ListView listViewPurchase;
    Button btnBackPurchase;
    TextView txtTotalSpent;
    SQLiteHelper dbHelper;
    String buyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        dbHelper = new SQLiteHelper(this);
        buyerId = getIntent().getStringExtra("id");

        listViewPurchase = findViewById(R.id.listViewPurchase);
        btnBackPurchase = findViewById(R.id.btnBackPurchase);
        txtTotalSpent = findViewById(R.id.txtTotalSpent);

        loadPurchaseHistory();

        btnBackPurchase.setOnClickListener(v -> {
            Intent intent = new Intent(this, BuyerDashboardActivity.class);
            intent.putExtra("id", buyerId);
            startActivity(intent);
            finish();
        });
    }

    private void loadPurchaseHistory() {
        ArrayList<PurchaseItem> historyList = new ArrayList<>();
        Cursor cursor = dbHelper.getPurchaseHistory(buyerId);
        double total = 0.0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("item_title"));
                String priceStr = cursor.getString(cursor.getColumnIndexOrThrow("item_price"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("item_desc"));
                String seller = cursor.getString(cursor.getColumnIndexOrThrow("seller"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                double price = 0.0;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (Exception ignored) {}

                total += price;

                historyList.add(new PurchaseItem(title, priceStr, desc, seller, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        txtTotalSpent.setText("Total Spent: RM " + String.format("%.2f", total));
        PurchaseHistoryAdapter adapter = new PurchaseHistoryAdapter(this, historyList);
        listViewPurchase.setAdapter(adapter);
    }
}
