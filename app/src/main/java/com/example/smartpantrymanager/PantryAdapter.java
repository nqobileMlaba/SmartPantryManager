package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private ArrayList<PantryItem> pantryItems;

    // ---------- The ViewHolder: holds the three TextViews of one row ----------
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

    // ---------- Constructor: receives the data to display ----------
    public PantryAdapter(ArrayList<PantryItem> pantryItems) {
        this.pantryItems = pantryItems;
    }

    // ---------- Builds an empty row from item_pantry.xml ----------
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    // ---------- Fills one row with the data at this position ----------
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryItems.get(position);

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setText("No expiry date");
        } else {
            holder.textExpiry.setText("Expires: " + item.getExpiryDate());
        }
    }

    // ---------- How many rows in total ----------
    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    // ---------- Replace the data and redraw the list ----------
    public void setPantryItems(ArrayList<PantryItem> newItems) {
        this.pantryItems = newItems;
        notifyDataSetChanged();
    }

    // ---------- Shows 6 instead of 6.0, but keeps 1.5 as 1.5 ----------
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}