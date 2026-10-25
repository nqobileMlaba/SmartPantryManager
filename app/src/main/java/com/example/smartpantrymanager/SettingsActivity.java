package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SmartPantryPreferences";
    public static final String KEY_EXPIRY_ALERT = "expiry_alert_enabled";

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

        Switch switchAlert = findViewById(R.id.switchExpiryAlert);

        // 1. Show the saved value (default: off)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        switchAlert.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERT, false));

        // 2. Save every time the user flips the switch
        switchAlert.setOnCheckedChangeListener((buttonView, isChecked) ->
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(KEY_EXPIRY_ALERT, isChecked)
                        .apply());

        NavHelper.setup(this);
    }
}