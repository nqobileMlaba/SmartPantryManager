package com.example.smartpantrymanager;

import java.util.ArrayList;

public class Recipe {
    private final long id;
    private final String name;
    private final String steps;
    private ArrayList<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
    public ArrayList<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(ArrayList<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}