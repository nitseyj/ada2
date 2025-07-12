package com.example.assm2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    private LinearLayout sellerContainer;
    private SQLiteHelper dbHelper;
    private SearchView searchView;
    private Cursor sellerCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new SQLiteHelper(this);
        sellerContainer = findViewById(R.id.sellerContainer);
        searchView = findViewById(R.id.searchView);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(this, "✅ Nearby sellers are up-to-date", Toast.LENGTH_SHORT).show();
        });

        sellerCursor = dbHelper.getSellersWithAddress();
        loadSellers("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadSellers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadSellers(newText);
                return true;
            }
        });
    }

    private void loadSellers(String keyword) {
        sellerContainer.removeAllViews();

        if (sellerCursor != null && sellerCursor.moveToFirst()) {
            int delay = 0;

            do {
                String name = sellerCursor.getString(sellerCursor.getColumnIndexOrThrow("name"));
                String phone = sellerCursor.getString(sellerCursor.getColumnIndexOrThrow("phone"));
                String address = sellerCursor.getString(sellerCursor.getColumnIndexOrThrow("address"));
                byte[] imageBytes = sellerCursor.getBlob(sellerCursor.getColumnIndexOrThrow("profile_pic"));

                // Keyword filter
                if (!keyword.isEmpty()) {
                    String lowerKeyword = keyword.toLowerCase();
                    if (!(name.toLowerCase().contains(lowerKeyword) ||
                            phone.toLowerCase().contains(lowerKeyword) ||
                            address.toLowerCase().contains(lowerKeyword))) {
                        continue;
                    }
                }

                // Card
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.HORIZONTAL);
                card.setPadding(32, 24, 16, 24);
                card.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(0, 0, 0, 32);
                card.setLayoutParams(cardParams);

                // Profile ImageView
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(160, 160);
                imageParams.setMargins(0, 0, 32, 0);
                imageView.setLayoutParams(imageParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.default_profile);
                }

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.OVAL);
                shape.setStroke(2, 0xFFCCCCCC);
                imageView.setBackground(shape);
                imageView.setClipToOutline(true);

                card.addView(imageView);

                // Text container
                LinearLayout textContainer = new LinearLayout(this);
                textContainer.setOrientation(LinearLayout.VERTICAL);
                textContainer.setGravity(Gravity.CENTER_VERTICAL);

                // Name Row
                LinearLayout nameLayout = new LinearLayout(this);
                nameLayout.setOrientation(LinearLayout.HORIZONTAL);
                nameLayout.setGravity(Gravity.CENTER_VERTICAL);
                nameLayout.setPadding(0, 0, 0, 8);

                ImageView nameIcon = new ImageView(this);
                nameIcon.setImageResource(R.drawable.ic_person); // 👤
                nameIcon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
                nameLayout.addView(nameIcon);

                TextView tvName = new TextView(this);
                tvName.setText(" " + name);
                tvName.setTextSize(16);
                nameLayout.addView(tvName);

                // Phone Row
                LinearLayout phoneLayout = new LinearLayout(this);
                phoneLayout.setOrientation(LinearLayout.HORIZONTAL);
                phoneLayout.setGravity(Gravity.CENTER_VERTICAL);
                phoneLayout.setPadding(0, 0, 0, 8);

                ImageView phoneIcon = new ImageView(this);
                phoneIcon.setImageResource(R.drawable.ic_phone);
                phoneIcon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
                phoneLayout.addView(phoneIcon);

                TextView tvPhone = new TextView(this);
                tvPhone.setText(" " + phone);
                tvPhone.setTextSize(14);
                tvPhone.setOnClickListener(v -> {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    startActivity(dialIntent);
                });
                phoneLayout.addView(tvPhone);

                // Address Row
                LinearLayout addressLayout = new LinearLayout(this);
                addressLayout.setOrientation(LinearLayout.HORIZONTAL);
                addressLayout.setGravity(Gravity.CENTER_VERTICAL);

                ImageView addressIcon = new ImageView(this);
                addressIcon.setImageResource(R.drawable.ic_location);
                addressIcon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
                addressLayout.addView(addressIcon);

                TextView tvAddress = new TextView(this);
                tvAddress.setText(" " + address);
                tvAddress.setTextSize(14);
                addressLayout.addView(tvAddress);

                textContainer.addView(nameLayout);
                textContainer.addView(phoneLayout);
                textContainer.addView(addressLayout);
                card.addView(textContainer);

                // Animation
                card.setAlpha(0f);
                card.setTranslationY(50);
                card.animate()
                        .alpha(1f)
                        .translationY(0)
                        .setDuration(400)
                        .setStartDelay(delay)
                        .start();

                delay += 100;

                sellerContainer.addView(card);
            } while (sellerCursor.moveToNext());
        }
    }
}
