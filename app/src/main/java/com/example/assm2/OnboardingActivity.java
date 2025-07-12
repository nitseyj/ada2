package com.example.assm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ViewPager2 viewPager = findViewById(R.id.onboardingViewPager);
        TabLayout tabLayout = findViewById(R.id.tabIndicator);
        Button buttonNext = findViewById(R.id.buttonNext);
        Button buttonSkip = findViewById(R.id.buttonSkip);

        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(R.drawable.reduce, "Reduce", "Reduce waste before it starts."));
        items.add(new OnboardingItem(R.drawable.reuse, "Reuse", "Reuse items again to save money and resources."));
        items.add(new OnboardingItem(R.drawable.recycle, "Recycle", "Recycle things to save the planet."));

        OnboardingAdapter adapter = new OnboardingAdapter(items);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setIcon(R.drawable.dot_selector)
        ).attach();


        buttonNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                goToMainScreen();
            }
        });

        buttonSkip.setOnClickListener(v -> goToMainScreen());
    }

    private void goToMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
