package com.example.assm2;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PricingActivity extends AppCompatActivity {

    private LinearLayout containerPricing;
    private TextView txtLastUpdated;
    private Button btnBackPricing, btnRefreshPricing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);

        // Find Views
        containerPricing = findViewById(R.id.containerPricing);
        txtLastUpdated = findViewById(R.id.txtLastUpdated);
        btnBackPricing = findViewById(R.id.btnBackPricing);
        btnRefreshPricing = findViewById(R.id.btnRefreshPricing);

        // Update date
        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        txtLastUpdated.setText("Last updated: " + date);

        // Load material cards
        loadMaterialCards();

        // Back button to go back to previous activity
        btnBackPricing.setOnClickListener(v -> finish());

        // Refresh button and show toast message
        btnRefreshPricing.setOnClickListener(v ->
                Toast.makeText(this, "Price is already up-to-date", Toast.LENGTH_SHORT).show());
    }

    private void loadMaterialCards() {
        containerPricing.removeAllViews();

        addMaterialCard(R.drawable.ic_metal, "Metal", "RM 0.80 / kg");
        addMaterialCard(R.drawable.ic_plastic, "Plastic", "RM 0.50 / kg");
        addMaterialCard(R.drawable.ic_paper, "Paper", "RM 0.30 / kg");
        addMaterialCard(R.drawable.ic_glass, "Glass", "RM 0.40 / kg");
        addMaterialCard(R.drawable.ic_electronic, "Electronics", "RM 10.20 / kg");
        addMaterialCard(R.drawable.ic_textile, "Textile", "RM 0.25 / kg");
        addMaterialCard(R.drawable.ic_battery, "Battery", "RM 4.50 / kg");
    }

    private void addMaterialCard(int iconResId, String title, String price) {
        // Card Layout
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.bg_card_white);
        card.setPadding(32, 32, 32, 32);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);
        card.setGravity(Gravity.CENTER_VERTICAL);

        // Icon
        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
        icon.setImageResource(iconResId);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setPadding(0, 0, 24, 0);

        // Texts
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView txtTitle = new TextView(this);
        txtTitle.setText(title);
        txtTitle.setTextColor(0xFF0D2B3E);
        txtTitle.setTextSize(18);
        txtTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtPrice = new TextView(this);
        txtPrice.setText(price);
        txtPrice.setTextColor(0xFF444444);
        txtPrice.setTextSize(14);

        // Assemble
        textContainer.addView(txtTitle);
        textContainer.addView(txtPrice);
        card.addView(icon);
        card.addView(textContainer);
        containerPricing.addView(card);
    }
}
