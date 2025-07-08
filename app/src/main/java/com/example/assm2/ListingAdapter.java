package com.example.assm2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ListingAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListingItem> listings;

    public ListingAdapter(Context context, ArrayList<ListingItem> listings) {
        this.context = context;
        this.listings = listings;
    }

    @Override
    public int getCount() {
        return listings.size();
    }

    @Override
    public Object getItem(int position) {
        return listings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // This is where the status (Available / Sold) is shown
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        }

        ListingItem item = listings.get(position);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtPriceQty = convertView.findViewById(R.id.txtPriceQty);
        TextView txtSeller = convertView.findViewById(R.id.txtSeller);
        TextView txtStatus = convertView.findViewById(R.id.txtStatus);

        txtTitle.setText(item.title);
        txtPriceQty.setText("RM " + item.price + " | " + item.quantity + " kg");
        txtSeller.setText("Seller: " + item.seller);

        // Set item status and color
        txtStatus.setText(item.available ? "Available" : "Sold");
        txtStatus.setTextColor(ContextCompat.getColor(context,
                item.available ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));

        return convertView;
    }
}
