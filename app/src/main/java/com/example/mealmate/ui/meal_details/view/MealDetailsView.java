package com.example.mealmate.ui.meal_details.view;

import com.example.mealmate.data.meals.models.Meal;

public interface MealDetailsView {
    void showLoading();

    void hideLoading();

    void showMeal(Meal meal);

    void onPlanAddedSuccess();

    void onPlanAddedError(String error);

    void showError(String message);
}
