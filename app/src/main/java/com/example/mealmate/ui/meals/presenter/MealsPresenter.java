package com.example.mealmate.ui.meals.presenter;

public interface MealsPresenter {
        void getMealsByCategory(String category);
        void onDestroy();
    }
