package com.example.mealmate.ui.meal_details.presenter;

import com.example.mealmate.data.meals.models.Meal;

public interface MealDetailsPresenter {
        void getMealDetails(Meal meal);
        void onDestroy();
    }
