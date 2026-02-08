package com.example.mealmate.ui.home.presenter;

import com.example.mealmate.data.meals.models.Meal;

import java.util.Date;

public interface HomePresenter {
    void getRandomMeal();
    void getCachedMeal(String currentDate);
    void onMealClicked(Meal meal);
    void getCategories();
    void getFavoritesCount();

    void onCookNowClicked(Meal meal);
    void onCookLaterClicked(Meal meal);

    void addToPlan(Meal meal, Date date);
    void addToPlan(Meal meal, Date date, String mealType);

    void getPlansCount();
    void getTodaysPlan();
    void logout();
    void onDestroy();
}