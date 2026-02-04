package com.example.mealmate.ui.meal_details.view;

import com.example.mealmate.data.meals.model.Meal;

public interface MealDetailsView {
        void showLoading();
        void hideLoading();
        void showMeal(Meal meal);
        void showError(String message);
    }
