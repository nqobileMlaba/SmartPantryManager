package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SmartPantryPreferences";
    public static final String KEY_EXPIRY_ALERT = "expiry_alert_enabled";
    public static final String KEY_EXPIRY_DAYS = "expiry_alert_days";

    private final Integer[] dayOptions = {1, 2, 3, 5, 7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        Switch switchAlert = findViewById(R.id.switchExpiryAlert);
        switchAlert.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERT, false));
        switchAlert.setOnCheckedChangeListener((buttonView, isChecked) ->
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit().putBoolean(KEY_EXPIRY_ALERT, isChecked).apply());


        Spinner spinnerDays = findViewById(R.id.spinnerExpiryDays);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, dayOptions);
        spinnerDays.setAdapter(adapter);

        int savedDays = prefs.getInt(KEY_EXPIRY_DAYS, 3);
        for (int i = 0; i < dayOptions.length; i++) {
            if (dayOptions[i] == savedDays) {
                spinnerDays.setSelection(i);
            }
        }

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit().putInt(KEY_EXPIRY_DAYS, dayOptions[position]).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        NavHelper.setup(this);
    }
}