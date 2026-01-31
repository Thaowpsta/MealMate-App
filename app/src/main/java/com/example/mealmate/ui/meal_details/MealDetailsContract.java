package com.example.mealmate.ui.meal_details;

import com.example.mealmate.data.models.Meal;

public interface MealDetailsContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showMeal(Meal meal);
        void showError(String message);
    }

    interface Presenter {
        void getMealDetails(Meal meal);
        void onDestroy();
    }
}