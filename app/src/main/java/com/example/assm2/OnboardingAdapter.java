package com.example.assm2;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_onboarding, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final ImageView image;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
            image = itemView.findViewById(R.id.imageOnboarding);
        }

        void bind(OnboardingItem item) {
            title.setText(item.getTitle());

            SpannableString spannable = new SpannableString(item.getDescription());
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#388E3C")), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            description.setText(spannable);

            image.setImageResource(item.getImage());

            Animation fadeIn = AnimationUtils.loadAnimation(itemView.getContext(), android.R.anim.fade_in);
            fadeIn.setDuration(600);

            title.startAnimation(fadeIn);
            description.startAnimation(fadeIn);
            image.startAnimation(fadeIn);
        }
    }
}
