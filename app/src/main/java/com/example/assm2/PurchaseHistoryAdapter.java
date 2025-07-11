package com.example.assm2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class PurchaseHistoryAdapter extends BaseAdapter {

    private final Activity context;
    private final ArrayList<PurchaseItem> items;

    public PurchaseHistoryAdapter(Activity context, ArrayList<PurchaseItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override public int getCount() { return items.size(); }
    @Override public Object getItem(int i) { return items.get(i); }
    @Override public long getItemId(int i) { return i; }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        PurchaseItem item = items.get(i);
        View view = LayoutInflater.from(context).inflate(R.layout.purchase_history_item, parent, false);

        ((TextView) view.findViewById(R.id.txtTitle)).setText("📦 " + item.title);
        ((TextView) view.findViewById(R.id.txtPrice)).setText("💲 RM " + item.price);
        ((TextView) view.findViewById(R.id.txtDesc)).setText("📝 " + item.desc);
        ((TextView) view.findViewById(R.id.txtSeller)).setText("👤 " + item.seller);
        ((TextView) view.findViewById(R.id.txtDate)).setText("📅 " + item.date);

        return view;
    }
}
