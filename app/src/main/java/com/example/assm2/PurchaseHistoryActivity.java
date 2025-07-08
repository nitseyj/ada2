package com.example.assm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PurchaseHistoryActivity extends AppCompatActivity {

    ListView listViewHistory;
    Button btnBackPurchaseHistory;
    SQLiteHelper dbHelper;
    String buyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        dbHelper = new SQLiteHelper(this);
        buyerId = getIntent().getStringExtra("id");

        listViewHistory = findViewById(R.id.listViewHistory);
        btnBackPurchaseHistory = findViewById(R.id.btnBackPurchaseHistory);

        loadPurchaseHistory();

        btnBackPurchaseHistory.setOnClickListener(v -> {
            Intent back = new Intent(PurchaseHistoryActivity.this, BuyerDashboardActivity.class);
            back.putExtra("id", buyerId);
            startActivity(back);
            finish();
        });
    }

    private void loadPurchaseHistory() {
        ArrayList<String> historyList = new ArrayList<>();
        Cursor cursor = dbHelper.getPurchaseHistory(buyerId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("item_title"));
                String price = cursor.getString(cursor.getColumnIndexOrThrow("item_price"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("item_desc"));
                String seller = cursor.getString(cursor.getColumnIndexOrThrow("seller"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                String entry = "Title: " + title +
                        "\nPrice: RM " + price +
                        "\nDescription: " + desc +
                        "\nSeller: " + seller +
                        "\nDate: " + date;

                historyList.add(entry);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            historyList.add("No purchase history found.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, historyList);
        listViewHistory.setAdapter(adapter);
    }
}
