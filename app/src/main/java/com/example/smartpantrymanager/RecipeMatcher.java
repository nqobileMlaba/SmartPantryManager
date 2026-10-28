package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeMatcher {


    public static ArrayList<Recipe> findMakeableRecipes(ArrayList<PantryItem> pantry,
                                                        ArrayList<Recipe> allRecipes) {
        HashMap<String, Double> stock = buildStock(pantry);
        ArrayList<Recipe> makeable = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (canMake(recipe, stock)) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }


    private static HashMap<String, Double> buildStock(ArrayList<PantryItem> pantry) {
        HashMap<String, Double> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double amount = toBaseAmount(item.getQuantity(), item.getUnit());

            if (stock.containsKey(key)) {
                stock.put(key, stock.get(key) + amount);
            } else {
                stock.put(key, amount);
            }
        }
        return stock;
    }

    // ALL ingredients must pass. One failure means the recipe is rejected.
    private static boolean canMake(Recipe recipe, HashMap<String, Double> stock) {
        if (recipe.getIngredients().isEmpty()) {
            return false;
        }
        for (RecipeIngredient needed : recipe.getIngredients()) {
            String key = makeKey(needed.getName(), needed.getUnit());

            if (!stock.containsKey(key)) {
                return false;
            }
            double have = stock.get(key);
            double need = toBaseAmount(needed.getQuantity(), needed.getUnit());
            if (have + 0.0001 < need) {
                return false;
            }
        }
        return true;
    }


    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + unitFamily(unit);
    }


    static String normalizeName(String name) {
        String n = name.trim().toLowerCase().replaceAll("\\s+", " ");
        if (n.endsWith("ies") && n.length() > 4) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("oes")) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("s") && !n.endsWith("ss")) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }


    private static String unitFamily(String unit) {
        String u = unit.trim().toLowerCase();
        if (u.equals("kg") || u.equals("g")) return "g";
        if (u.equals("litres") || u.equals("ml")) return "ml";
        return u;
    }

    private static double toBaseAmount(double quantity, String unit) {
        String u = unit.trim().toLowerCase();
        if (u.equals("kg") || u.equals("litres")) {
            return quantity * 1000;
        }
        return quantity;
    }
}