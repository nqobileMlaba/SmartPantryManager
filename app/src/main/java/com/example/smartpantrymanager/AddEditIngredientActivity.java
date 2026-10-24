package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

    // The only units the user can choose from
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

    private void saveIngredient() {
        if (!validateInput()) {
            return;   // stop here: errors are already shown on the fields
        }

        String name = editName.getText().toString().trim();
        double quantity = Double.parseDouble(editQuantity.getText().toString().trim());
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = editExpiry.getText().toString().trim();
        if (expiry.isEmpty()) {
            expiry = null;   // optional field left blank
        }

        PantryItem item = new PantryItem(0, name, quantity, unit, expiry);

        boolean wasSuccessful = false;
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            wasSuccessful = ds.insertPantryItem(item);
            ds.close();
        } catch (Exception e) {
            wasSuccessful = false;
        }

        if (wasSuccessful) {
            Toast.makeText(this, name + " added to your pantry", Toast.LENGTH_SHORT).show();
            finish();   // close this screen and go back to the Pantry List
        } else {
            Toast.makeText(this, "Could not save. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    // Returns true only if every field is acceptable. Shows ALL errors at once.
    private boolean validateInput() {
        boolean isValid = true;

        // 1. Name must not be empty
        String name = editName.getText().toString().trim();
        if (name.isEmpty()) {
            editName.setError("Please enter the ingredient name");
            isValid = false;
        }

        // 2. Quantity must be a number greater than 0
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
                }
            } catch (NumberFormatException e) {
                editQuantity.setError("Enter a valid number, e.g. 2 or 1.5");
                isValid = false;
            }
        }

        // 3. Expiry date is optional, but if typed it must be a real date
        String expiry = editExpiry.getText().toString().trim();
        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            editExpiry.setError("Use the format YYYY-MM-DD, e.g. 2026-11-20");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidDate(String text) {
        if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;   // wrong shape
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);   // reject dates like 2026-02-31
        try {
            format.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}