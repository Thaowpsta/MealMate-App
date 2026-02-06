package com.example.mealmate.ui.home.presenter;

import com.example.mealmate.data.meals.models.Meal;

import java.util.Date;

public interface HomePresenter {
    void getRandomMeal();
    void getCachedMeal(String currentDate);
    void onMealClicked(Meal meal);
    void getCategories();
    void getFavoritesCount();

    // (Cook Now)
    void addToPlan(Meal meal, Date date);

    // (Cook Later)
    void addToPlan(Meal meal, Date date, String mealType);
    void getPlansCount();
    void getTodaysPlan();
    void logout();
    void onDestroy();
}
