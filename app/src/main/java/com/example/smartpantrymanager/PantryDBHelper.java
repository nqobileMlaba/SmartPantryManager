package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smartpantry.db";
    public static final int DATABASE_VERSION = 2;   // raised from 1 so recipes get added

    //  Table 1: PantryItem
    public static final String TABLE_PANTRY = "PantryItem";
    public static final String COL_PANTRY_ID = "_id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Table 2: Recipe
    public static final String TABLE_RECIPE = "Recipe";
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "preparation_steps";

    //  Table 3: RecipeIngredient
    public static final String TABLE_RECIPE_INGREDIENT = "RecipeIngredient";
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "required_quantity";
    public static final String COL_RI_UNIT = "required_unit";

    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PANTRY_NAME + " TEXT NOT NULL, "
                    + COL_PANTRY_QUANTITY + " REAL NOT NULL, "
                    + COL_PANTRY_UNIT + " TEXT NOT NULL, "
                    + COL_PANTRY_EXPIRY + " TEXT);";

    private static final String CREATE_TABLE_RECIPE =
            "CREATE TABLE " + TABLE_RECIPE + " ("
                    + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RECIPE_NAME + " TEXT NOT NULL, "
                    + COL_RECIPE_STEPS + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_RECIPE_INGREDIENT =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENT + " ("
                    + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                    + COL_RI_INGREDIENT_NAME + " TEXT NOT NULL, "
                    + COL_RI_QUANTITY + " REAL NOT NULL, "
                    + COL_RI_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + COL_RI_RECIPE_ID + ") REFERENCES "
                    + TABLE_RECIPE + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE);";

    public PantryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Runs ONCE on a brand-new install: build the tables, then add the recipes
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPE);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENT);
        seedRecipes(db);
    }

    // Runs when the version goes up. Pantry items are NOT touched.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.delete(TABLE_RECIPE, null, null);   // clear any old recipes (ingredients cascade)
            seedRecipes(db);
        }
    }

    // ---------- Helpers that insert one recipe / one ingredient ----------
    private long addRecipe(SQLiteDatabase db, String name, String steps) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_RECIPE_STEPS, steps);
        return db.insert(TABLE_RECIPE, null, values);   // returns the new recipe's id
    }

    private void addIng(SQLiteDatabase db, long recipeId, String name, double qty, String unit) {
        ContentValues values = new ContentValues();
        values.put(COL_RI_RECIPE_ID, recipeId);
        values.put(COL_RI_INGREDIENT_NAME, name);
        values.put(COL_RI_QUANTITY, qty);
        values.put(COL_RI_UNIT, unit);
        db.insert(TABLE_RECIPE_INGREDIENT, null, values);
    }

    // ---------- The 18 recipes ----------
    private void seedRecipes(SQLiteDatabase db) {
        long id;

        id = addRecipe(db, "Scrambled Eggs on Toast",
                "1. Beat the eggs in a bowl.\n2. Melt the butter in a pan.\n3. Pour in the eggs and stir until cooked.\n4. Toast the bread and serve together.");
        addIng(db, id, "egg", 2, "pieces");
        addIng(db, id, "bread", 2, "slices");
        addIng(db, id, "butter", 1, "tbsp");

        id = addRecipe(db, "Pancakes",
                "1. Mix the flour, sugar and egg.\n2. Slowly add the milk and whisk until smooth.\n3. Pour a small amount into a hot greased pan.\n4. Cook until golden on both sides.");
        addIng(db, id, "flour", 200, "g");
        addIng(db, id, "milk", 300, "ml");
        addIng(db, id, "egg", 1, "pieces");
        addIng(db, id, "sugar", 2, "tbsp");

        id = addRecipe(db, "French Toast",
                "1. Whisk the eggs, milk and sugar.\n2. Dip each bread slice in the mixture.\n3. Fry in a hot pan until golden on both sides.");
        addIng(db, id, "bread", 4, "slices");
        addIng(db, id, "egg", 2, "pieces");
        addIng(db, id, "milk", 100, "ml");
        addIng(db, id, "sugar", 1, "tbsp");

        id = addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt the butter in a pan.\n3. Pour in the eggs and cook gently.\n4. Add the cheese, fold in half and serve.");
        addIng(db, id, "egg", 3, "pieces");
        addIng(db, id, "butter", 1, "tbsp");
        addIng(db, id, "cheese", 50, "g");

        id = addRecipe(db, "Cheese Toast",
                "1. Butter the bread.\n2. Place the cheese on top.\n3. Grill or bake until the cheese melts.");
        addIng(db, id, "bread", 2, "slices");
        addIng(db, id, "cheese", 60, "g");
        addIng(db, id, "butter", 1, "tbsp");

        id = addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta in salted water until soft.\n2. Fry the chopped onion in oil.\n3. Add the chopped tomatoes and cook for 10 minutes.\n4. Mix the sauce with the pasta.");
        addIng(db, id, "pasta", 200, "g");
        addIng(db, id, "tomato", 3, "pieces");
        addIng(db, id, "onion", 1, "pieces");
        addIng(db, id, "oil", 2, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the chopped onion in oil.\n3. Add the eggs and scramble.\n4. Add the rice and salt, and stir well.");
        addIng(db, id, "rice", 2, "cups");
        addIng(db, id, "egg", 2, "pieces");
        addIng(db, id, "onion", 1, "pieces");
        addIng(db, id, "oil", 2, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Rice and Beans",
                "1. Cook the rice.\n2. Fry the onion in oil.\n3. Add the beans and salt and heat through.\n4. Serve the beans over the rice.");
        addIng(db, id, "rice", 1, "cups");
        addIng(db, id, "bean", 1, "cups");
        addIng(db, id, "onion", 1, "pieces");
        addIng(db, id, "oil", 1, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash.\n3. Mix in the butter, milk and salt.");
        addIng(db, id, "potato", 4, "pieces");
        addIng(db, id, "butter", 2, "tbsp");
        addIng(db, id, "milk", 100, "ml");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Roast Potatoes",
                "1. Heat the oven to 200 degrees.\n2. Cut the potatoes into pieces.\n3. Toss with oil and salt.\n4. Bake for 40 minutes until crispy.");
        addIng(db, id, "potato", 4, "pieces");
        addIng(db, id, "oil", 2, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Tomato Soup",
                "1. Fry the chopped onion in butter.\n2. Add the chopped tomatoes and salt.\n3. Simmer for 20 minutes.\n4. Blend until smooth.");
        addIng(db, id, "tomato", 5, "pieces");
        addIng(db, id, "onion", 1, "pieces");
        addIng(db, id, "butter", 1, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix in the eggs and flour.\n3. Fry spoonfuls in a hot pan until golden.");
        addIng(db, id, "banana", 2, "pieces");
        addIng(db, id, "egg", 2, "pieces");
        addIng(db, id, "flour", 100, "g");

        id = addRecipe(db, "Chicken and Rice",
                "1. Fry the onion in oil.\n2. Add the chicken pieces and salt and cook through.\n3. Add the cooked rice and mix well.");
        addIng(db, id, "chicken", 300, "g");
        addIng(db, id, "rice", 1, "cups");
        addIng(db, id, "onion", 1, "pieces");
        addIng(db, id, "oil", 1, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Vegetable Stir Fry",
                "1. Slice the carrots, onion and cabbage.\n2. Heat the oil in a pan.\n3. Stir fry the vegetables with salt for 8 minutes.");
        addIng(db, id, "carrot", 2, "pieces");
        addIng(db, id, "onion", 1, "pieces");
        addIng(db, id, "cabbage", 200, "g");
        addIng(db, id, "oil", 2, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Rice Pudding",
                "1. Put the rice and milk in a pot.\n2. Simmer gently, stirring often, until thick.\n3. Stir in the sugar and serve.");
        addIng(db, id, "rice", 1, "cups");
        addIng(db, id, "milk", 500, "ml");
        addIng(db, id, "sugar", 3, "tbsp");

        id = addRecipe(db, "Flatbread",
                "1. Mix the flour, oil and salt with a little water into a dough.\n2. Roll into flat rounds.\n3. Cook in a dry hot pan for 2 minutes each side.");
        addIng(db, id, "flour", 300, "g");
        addIng(db, id, "oil", 2, "tbsp");
        addIng(db, id, "salt", 1, "tsp");

        id = addRecipe(db, "Peanut Butter Sandwich",
                "1. Spread the peanut butter on one slice of bread.\n2. Cover with the second slice and serve.");
        addIng(db, id, "bread", 2, "slices");
        addIng(db, id, "peanut butter", 2, "tbsp");

        id = addRecipe(db, "Beans on Toast",
                "1. Heat the beans in a pot.\n2. Toast and butter the bread.\n3. Pour the beans over the toast.");
        addIng(db, id, "bean", 1, "cups");
        addIng(db, id, "bread", 2, "slices");
        addIng(db, id, "butter", 1, "tbsp");
    }
}