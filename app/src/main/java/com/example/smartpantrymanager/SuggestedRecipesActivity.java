package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvRecipes;
    private TextView textNoRecipes;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.suggestedRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvRecipes = findViewById(R.id.rvRecipes);
        textNoRecipes = findViewById(R.id.textNoRecipes);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(new ArrayList<Recipe>());
        rvRecipes.setAdapter(recipeAdapter);

        recipeAdapter.setOnRecipeClickListener(recipe -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
        });

        NavHelper.setup(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        ArrayList<Recipe> makeable = new ArrayList<>();
        try {
            PantryDataSource pantryDs = new PantryDataSource(this);
            pantryDs.open();
            ArrayList<PantryItem> pantry = pantryDs.getAllPantryItems();
            pantryDs.close();

            RecipeDataSource recipeDs = new RecipeDataSource(this);
            recipeDs.open();
            ArrayList<Recipe> allRecipes = recipeDs.getAllRecipes();
            recipeDs.close();

            makeable = RecipeMatcher.findMakeableRecipes(pantry, allRecipes);
        } catch (Exception e) {
            Toast.makeText(this, "Could not load recipes", Toast.LENGTH_LONG).show();
        }

        recipeAdapter.setRecipes(makeable);

        if (makeable.isEmpty()) {
            textNoRecipes.setVisibility(View.VISIBLE);
            rvRecipes.setVisibility(View.GONE);
        } else {
            textNoRecipes.setVisibility(View.GONE);
            rvRecipes.setVisibility(View.VISIBLE);
        }
    }
}