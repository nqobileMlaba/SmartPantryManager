package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonBack = findViewById(R.id.buttonDetailBack);
        buttonBack.setOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra("recipeId", -1);
        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        showRecipe(recipeId);
    }

    private void showRecipe(long recipeId) {
        Recipe recipe;
        try {
            RecipeDataSource ds = new RecipeDataSource(this);
            ds.open();
            recipe = ds.getRecipeById(recipeId);
            ds.close();
        } catch (Exception e) {
            recipe = null;
        }

        if (recipe == null) {
            Toast.makeText(this, "Could not load this recipe", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.textDetailName)).setText(recipe.getName());

        StringBuilder list = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            list.append("• ")
                    .append(formatQuantity(ing.getQuantity()))
                    .append(" ")
                    .append(ing.getUnit())
                    .append(" ")
                    .append(ing.getName())
                    .append("\n");
        }
        ((TextView) findViewById(R.id.textDetailIngredients)).setText(list.toString().trim());
        ((TextView) findViewById(R.id.textDetailSteps)).setText(recipe.getSteps());
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
