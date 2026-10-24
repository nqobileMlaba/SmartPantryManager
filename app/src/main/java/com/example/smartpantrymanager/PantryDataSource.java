package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class PantryDataSource {

    private SQLiteDatabase database;
    private final PantryDBHelper dbHelper;

    public PantryDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // READ: get every pantry item, sorted by name (A to Z)
    public ArrayList<PantryItem> getAllPantryItems() {
        ArrayList<PantryItem> items = new ArrayList<>();

        Cursor cursor = database.query(
                PantryDBHelper.TABLE_PANTRY,     // which table
                null,                            // null = all columns
                null, null, null, null,          // no WHERE, GROUP BY or HAVING
                PantryDBHelper.COL_PANTRY_NAME + " COLLATE NOCASE ASC"); // ORDER BY

        while (cursor.moveToNext()) {
            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_ID));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_NAME));
            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_QUANTITY));
            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_UNIT));

            int expiryIndex = cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_EXPIRY);
            String expiry = cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex);

            items.add(new PantryItem(id, name, quantity, unit, expiry));
        }
        cursor.close();
        return items;
    }

    // CREATE: add one pantry item (we use it now for test data, and fully in Step 7)
    public boolean insertPantryItem(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(PantryDBHelper.COL_PANTRY_NAME, item.getName());
        values.put(PantryDBHelper.COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(PantryDBHelper.COL_PANTRY_UNIT, item.getUnit());
        if (item.getExpiryDate() == null) {
            values.putNull(PantryDBHelper.COL_PANTRY_EXPIRY);
        } else {
            values.put(PantryDBHelper.COL_PANTRY_EXPIRY, item.getExpiryDate());
        }
        long newRowId = database.insert(PantryDBHelper.TABLE_PANTRY, null, values);
        return newRowId != -1;
    }

    // READ ONE: get a single pantry item by its id (returns null if not found)
    public PantryItem getSpecificItem(long itemId) {
        PantryItem item = null;

        Cursor cursor = database.query(
                PantryDBHelper.TABLE_PANTRY,
                null,
                PantryDBHelper.COL_PANTRY_ID + " = ?",       // WHERE _id = ?
                new String[]{String.valueOf(itemId)},        // the value for the ?
                null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_NAME));
            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_QUANTITY));
            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_UNIT));

            int expiryIndex = cursor.getColumnIndexOrThrow(PantryDBHelper.COL_PANTRY_EXPIRY);
            String expiry = cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex);

            item = new PantryItem(itemId, name, quantity, unit, expiry);
        }
        cursor.close();
        return item;
    }

    // UPDATE: change an existing pantry item
    public boolean updatePantryItem(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(PantryDBHelper.COL_PANTRY_NAME, item.getName());
        values.put(PantryDBHelper.COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(PantryDBHelper.COL_PANTRY_UNIT, item.getUnit());
        if (item.getExpiryDate() == null) {
            values.putNull(PantryDBHelper.COL_PANTRY_EXPIRY);
        } else {
            values.put(PantryDBHelper.COL_PANTRY_EXPIRY, item.getExpiryDate());
        }

        int rowsChanged = database.update(
                PantryDBHelper.TABLE_PANTRY,
                values,
                PantryDBHelper.COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});

        return rowsChanged > 0;
    }

    // DELETE: remove one pantry item by its id
    public boolean deletePantryItem(long itemId) {
        int rowsDeleted = database.delete(
                PantryDBHelper.TABLE_PANTRY,
                PantryDBHelper.COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );
        return rowsDeleted > 0;
    }
}
