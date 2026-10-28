package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private ArrayList<PantryItem> pantryItems;
    private OnItemClickListener clickListener;

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        public final TextView textName;
        public final TextView textQuantity;
        public final TextView textExpiry;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textQuantity = itemView.findViewById(R.id.textItemQuantity);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
        }
    }

    public PantryAdapter(ArrayList<PantryItem> pantryItems) {
        this.pantryItems = pantryItems;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryItems.get(position);

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        boolean alertOn = holder.itemView.getContext()
                .getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(SettingsActivity.KEY_EXPIRY_ALERT, false);

        int alertDays = holder.itemView.getContext()
                        .getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
                        .getInt(SettingsActivity.KEY_EXPIRY_DAYS, 3);

        holder.textExpiry.setTextColor(holder.textName.getTextColors().getDefaultColor());

        if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setText("No expiry date");
        } else {
            long days = daysUntil(item.getExpiryDate());
            if (alertOn && days < 0) {
                holder.textExpiry.setText("Expired! " + item.getExpiryDate());
                holder.textExpiry.setTextColor(Color.RED);
            } else if (alertOn && days <= alertDays) {
                holder.textExpiry.setText("Expiring soon! " + item.getExpiryDate());
                holder.textExpiry.setTextColor(Color.RED);
            } else {
                holder.textExpiry.setText("Expires: " + item.getExpiryDate());
            }
        }


        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public void setPantryItems(ArrayList<PantryItem> newItems) {
        this.pantryItems = newItems;
        notifyDataSetChanged();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    // Days from now until the date (negative = already past).
    private long daysUntil(String dateText) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date expiry = format.parse(dateText);
            double days = (expiry.getTime() - System.currentTimeMillis()) / (1000.0 * 60 * 60 * 24);
            return (long) Math.ceil(days);
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

}