package com.example.mealmate.ui.home.presenter;

import com.example.mealmate.data.meals.model.Meal;

public interface HomePresenter {
        void getRandomMeal();
        void onMealClicked(Meal meal);
        void getCategories();
        void onDestroy();
    }
