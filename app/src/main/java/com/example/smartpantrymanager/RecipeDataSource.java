package com.example.smartpantrymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class RecipeDataSource {

    private SQLiteDatabase database;
    private final PantryDBHelper dbHelper;

    public RecipeDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // All recipes, each already filled with its ingredient list
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();

        Cursor cursor = database.query(
                PantryDBHelper.TABLE_RECIPE, null, null, null, null, null,
                PantryDBHelper.COL_RECIPE_NAME + " ASC");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RECIPE_ID));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RECIPE_NAME));
            String steps = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RECIPE_STEPS));

            Recipe recipe = new Recipe(id, name, steps);
            recipe.setIngredients(getIngredientsForRecipe(id));
            recipes.add(recipe);
        }
        cursor.close();
        return recipes;
    }

    // One recipe by id (used by Recipe Detail in the next step)
    public Recipe getRecipeById(long recipeId) {
        Recipe recipe = null;
        Cursor cursor = database.query(
                PantryDBHelper.TABLE_RECIPE, null,
                PantryDBHelper.COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RECIPE_NAME));
            String steps = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RECIPE_STEPS));
            recipe = new Recipe(recipeId, name, steps);
            recipe.setIngredients(getIngredientsForRecipe(recipeId));
        }
        cursor.close();
        return recipe;
    }

    // All ingredient rows where recipe_id matches
    private ArrayList<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        ArrayList<RecipeIngredient> list = new ArrayList<>();

        Cursor cursor = database.query(
                PantryDBHelper.TABLE_RECIPE_INGREDIENT, null,
                PantryDBHelper.COL_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RI_INGREDIENT_NAME));
            double qty = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RI_QUANTITY));
            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(PantryDBHelper.COL_RI_UNIT));
            list.add(new RecipeIngredient(name, qty, unit));
        }
        cursor.close();
        return list;
    }
}