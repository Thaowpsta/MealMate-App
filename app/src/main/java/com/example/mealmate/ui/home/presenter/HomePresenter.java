package com.example.mealmate.ui.home.presenter;

import com.example.mealmate.data.meals.models.Meal;

public interface HomePresenter {
    void getRandomMeal();
    void getCachedMeal(String currentDate);
    void onMealClicked(Meal meal);
    void getCategories();
    void logout();
    void onDestroy();
}
