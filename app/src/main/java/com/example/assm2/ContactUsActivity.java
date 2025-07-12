package com.example.assm2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ContactUsActivity extends AppCompatActivity {

    LinearLayout phoneLayout, emailLayout, fbLayout, instaLayout, twitterLayout;
    Button btnBackContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Find views
        phoneLayout = findViewById(R.id.layoutPhone);
        emailLayout = findViewById(R.id.layoutEmail);
        fbLayout = findViewById(R.id.layoutFacebook);
        instaLayout = findViewById(R.id.layoutInstagram);
        twitterLayout = findViewById(R.id.layoutTwitter);
        btnBackContact = findViewById(R.id.btnBackContact);

        // Back button click
        btnBackContact.setOnClickListener(v -> {
            finish();
        });

        // Phone click
        phoneLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+60123456789"));
            startActivity(intent);
        });

        // Email click
        emailLayout.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@recyclebersama.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Inquiry");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        // Facebook click
        fbLayout.setOnClickListener(v -> {
            Intent fbIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/recycling.my/"));
            startActivity(fbIntent);
        });

        // Instagram click
        instaLayout.setOnClickListener(v -> {
            Intent igIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/recycling/"));
            startActivity(igIntent);
        });

        // Twitter click
        twitterLayout.setOnClickListener(v -> {
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://x.com/recycle_now?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor"));
            startActivity(twitterIntent);
        });
    }
}
