package com.example.smartpantrymanager;

import android.content.Intent;
import android.widget.Button;

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

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPantry;
    private TextView textEmpty;
    private PantryAdapter pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvPantry = findViewById(R.id.rvPantry);
        textEmpty = findViewById(R.id.textEmpty);

        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        pantryAdapter = new PantryAdapter(new ArrayList<PantryItem>());
        rvPantry.setAdapter(pantryAdapter);
        pantryAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            intent.putExtra("pantryItemId", item.getId());
            startActivity(intent);
        });
        initAddButton();
        initRecipesButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();   // refresh the list every time the screen becomes visible
    }

    private void loadPantryItems() {
        ArrayList<PantryItem> items = new ArrayList<>();
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            items = ds.getAllPantryItems();
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Could not load pantry items", Toast.LENGTH_LONG).show();
        }

        pantryAdapter.setPantryItems(items);

        if (items.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
        }
    }

    private void initAddButton() {
        Button buttonAdd = findViewById(R.id.buttonAddIngredient);
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });
    }

    private void initRecipesButton() {
        Button buttonRecipes = findViewById(R.id.buttonSuggested);
        buttonRecipes.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
        });
    }


}