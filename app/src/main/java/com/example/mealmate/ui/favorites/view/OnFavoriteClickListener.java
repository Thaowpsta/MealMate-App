package com.example.mealmate.ui.favorites.view;

import com.example.mealmate.data.meals.models.Meal;

public interface OnFavoriteClickListener {
    void onRemoveFavorite(Meal meal);
    void onMealClick(Meal meal);
}
