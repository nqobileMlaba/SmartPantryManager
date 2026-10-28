package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class NavHelper {

    public static void setup(Activity activity) {
        activity.findViewById(R.id.navPantry).setOnClickListener(v ->
                go(activity, MainActivity.class));
        activity.findViewById(R.id.navRecipes).setOnClickListener(v ->
                go(activity, SuggestedRecipesActivity.class));
        activity.findViewById(R.id.navSettings).setOnClickListener(v ->
                go(activity, SettingsActivity.class));
    }

    private static void go(Activity from, Class<?> target) {
        if (from.getClass() == target) {
            return;
        }
        Intent intent = new Intent(from, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        from.startActivity(intent);
    }
}
