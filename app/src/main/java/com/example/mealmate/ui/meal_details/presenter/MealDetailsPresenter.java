package com.example.mealmate.ui.meal_details.presenter;

import com.example.mealmate.data.meals.model.Meal;

public interface MealDetailsPresenter {
        void getMealDetails(Meal meal);
        void onDestroy();
    }
