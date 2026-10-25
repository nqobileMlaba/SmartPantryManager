# Smart Pantry Manager

An Android app (Java) that helps reduce food waste. The user records the ingredients
they already have, and the app suggests only the recipes they can make right now.

## Features
- Add, edit, delete and view pantry items (name, quantity, unit, optional expiry date)
- 18 built-in recipes with ingredients and preparation steps
- Strict recipe matching: a recipe is suggested only when EVERY ingredient is in the
  pantry in a sufficient quantity. Recipes missing even one ingredient are never shown.
- Recipe detail screen
- Settings screen with an expiring-soon alert (saved with SharedPreferences)
- Bottom navigation between Pantry, Recipes and Settings

## Chosen database: SQLite
The app uses SQLite through `SQLiteOpenHelper` (the approach taught in the Mobile App
Development 700 learner guide). Pantry items, recipes and recipe ingredients are stored
in three related tables. The single settings toggle is stored in SharedPreferences.

### Why SQLite
- Built into Android, so no extra libraries are needed
- Handles many records and relationships (recipe -> ingredients)
- Data stays on the device and survives closing the app
- Matches the module material, unlike Room which is not covered

## Tables
- PantryItem (_id, name, quantity, unit, expiry_date)
- Recipe (_id, name, preparation_steps)
- RecipeIngredient (_id, recipe_id -> Recipe._id, ingredient_name, required_quantity, required_unit)

## How to run
1. Install Android Studio and open this project folder.
2. Let Gradle sync finish (internet needed the first time).
3. Connect an Android phone with USB debugging on, or start an emulator (API 24 or higher).
4. Click Run.

## Tech
Java, Android Studio, SQLite, RecyclerView with custom adapters, Intents.

## Author
NPA XIMBA (402311681)