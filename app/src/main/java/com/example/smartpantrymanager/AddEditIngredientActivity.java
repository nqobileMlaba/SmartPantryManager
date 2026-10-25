package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editQuantity;
    private EditText editExpiry;
    private Spinner spinnerUnit;

    // -1 means "adding a new item". Any other number means "editing that item".
    private long currentItemId = -1;

    private final String[] units = {
            "pieces", "g", "kg", "ml", "litres", "cups", "tbsp", "tsp", "slices"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        initUnitSpinner();
        initSaveButton();
        initCancelButton();
        initDeleteButton();

        long passedId = getIntent().getLongExtra("pantryItemId", -1);
        if (passedId != -1) {
            currentItemId = passedId;
            loadItemForEditing(passedId);
        }
    }

    private void initUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, units);
        spinnerUnit.setAdapter(adapter);
    }

    private void initSaveButton() {
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> saveIngredient());
    }

    private void initCancelButton() {
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void initDeleteButton() {
        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Are you sure you want to delete "
                        + editName.getText().toString().trim() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem() {
        boolean wasSuccessful;
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            wasSuccessful = ds.deletePantryItem(currentItemId);
            ds.close();
        } catch (Exception e) {
            wasSuccessful = false;
        }

        if (wasSuccessful) {
            Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not delete. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadItemForEditing(long itemId) {
        PantryItem item;
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            item = ds.getSpecificItem(itemId);
            ds.close();
        } catch (Exception e) {
            item = null;
        }

        if (item == null) {
            Toast.makeText(this, "Could not load this ingredient", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView textFormTitle = findViewById(R.id.textFormTitle);
        textFormTitle.setText("Edit Ingredient");
        findViewById(R.id.buttonDelete).setVisibility(View.VISIBLE);

        editName.setText(item.getName());
        editQuantity.setText(formatQuantity(item.getQuantity()));
        spinnerUnit.setSelection(findUnitPosition(item.getUnit()));
        if (item.getExpiryDate() != null) {
            editExpiry.setText(item.getExpiryDate());
        }
    }

    private int findUnitPosition(String unit) {
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(unit)) {
                return i;
            }
        }
        return 0;
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    private void saveIngredient() {
        if (!validateInput()) {
            return;
        }

        String name = editName.getText().toString().trim();
        double quantity = Double.parseDouble(editQuantity.getText().toString().trim());
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = editExpiry.getText().toString().trim();
        if (expiry.isEmpty()) {
            expiry = null;
        }

        PantryItem item = new PantryItem(currentItemId, name, quantity, unit, expiry);

        boolean wasSuccessful;
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            if (currentItemId == -1) {
                wasSuccessful = ds.insertPantryItem(item);
            } else {
                wasSuccessful = ds.updatePantryItem(item);
            }
            ds.close();
        } catch (Exception e) {
            wasSuccessful = false;
        }

        if (wasSuccessful) {
            String message = (currentItemId == -1)
                    ? name + " added to your pantry"
                    : name + " updated";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        String name = editName.getText().toString().trim();
        if (name.isEmpty()) {
            editName.setError("Please enter the ingredient name");
            isValid = false;
        } else if (name.length() < 2) {
            editName.setError("Name is too short");
            isValid = false;
        }

        String quantityText = editQuantity.getText().toString().trim();
        if (quantityText.isEmpty()) {
            editQuantity.setError("Please enter a quantity");
            isValid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    editQuantity.setError("Quantity must be greater than 0");
                    isValid = false;
                } else if (quantity > 100000) {
                    editQuantity.setError("Quantity is too large");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editQuantity.setError("Enter a valid number, e.g. 2 or 1.5");
                isValid = false;
            }
        }

        String expiry = editExpiry.getText().toString().trim();
        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            editExpiry.setError("Use the format YYYY-MM-DD, e.g. 2026-11-20");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidDate(String text) {
        if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);
        try {
            format.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}