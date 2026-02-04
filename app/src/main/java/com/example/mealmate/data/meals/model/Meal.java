package com.example.mealmate.data.meals.model;

import androidx.core.util.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Meal implements Serializable {
    public String strMeal;
    public String strCategory;
    public String strArea;
    public String strMealThumb;
    public String strInstructions;
    public String strYoutube;

    public String strIngredient1;
    public String strIngredient2;
    public String strIngredient3;
    public String strIngredient4;
    public String strIngredient5;
    public String strIngredient6;
    public String strIngredient7;
    public String strIngredient8;
    public String strIngredient9;
    public String strIngredient10;
    public String strIngredient11;
    public String strIngredient12;
    public String strIngredient13;
    public String strIngredient14;
    public String strIngredient15;
    public String strIngredient16;
    public String strIngredient17;
    public String strIngredient18;
    public String strIngredient19;
    public String strIngredient20;

    public String strMeasure1;
    public String strMeasure2;
    public String strMeasure3;
    public String strMeasure4;
    public String strMeasure5;
    public String strMeasure6;
    public String strMeasure7;
    public String strMeasure8;
    public String strMeasure9;
    public String strMeasure10;
    public String strMeasure11;
    public String strMeasure12;
    public String strMeasure13;
    public String strMeasure14;
    public String strMeasure15;
    public String strMeasure16;
    public String strMeasure17;
    public String strMeasure18;
    public String strMeasure19;
    public String strMeasure20;

    public String getStrMeal() {
        return strMeal;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public List<Pair<String, String>> getIngredientsAndMeasures() {
        List<Pair<String, String>> ingredients = new ArrayList<>();
        addIngredientIfNotNull(ingredients, strIngredient1, strMeasure1);
        addIngredientIfNotNull(ingredients, strIngredient2, strMeasure2);
        addIngredientIfNotNull(ingredients, strIngredient3, strMeasure3);
        addIngredientIfNotNull(ingredients, strIngredient4, strMeasure4);
        addIngredientIfNotNull(ingredients, strIngredient5, strMeasure5);
        addIngredientIfNotNull(ingredients, strIngredient6, strMeasure6);
        addIngredientIfNotNull(ingredients, strIngredient7, strMeasure7);
        addIngredientIfNotNull(ingredients, strIngredient8, strMeasure8);
        addIngredientIfNotNull(ingredients, strIngredient9, strMeasure9);
        addIngredientIfNotNull(ingredients, strIngredient10, strMeasure10);
        addIngredientIfNotNull(ingredients, strIngredient11, strMeasure11);
        addIngredientIfNotNull(ingredients, strIngredient12, strMeasure12);
        addIngredientIfNotNull(ingredients, strIngredient13, strMeasure13);
        addIngredientIfNotNull(ingredients, strIngredient14, strMeasure14);
        addIngredientIfNotNull(ingredients, strIngredient15, strMeasure15);
        addIngredientIfNotNull(ingredients, strIngredient16, strMeasure16);
        addIngredientIfNotNull(ingredients, strIngredient17, strMeasure17);
        addIngredientIfNotNull(ingredients, strIngredient18, strMeasure18);
        addIngredientIfNotNull(ingredients, strIngredient19, strMeasure19);
        addIngredientIfNotNull(ingredients, strIngredient20, strMeasure20);
        return ingredients;
    }

    private void addIngredientIfNotNull(List<Pair<String, String>> list, String ingredient, String measure) {
        if (ingredient != null && !ingredient.trim().isEmpty()) {
            list.add(new Pair<>(ingredient, measure));
        }
    }
}