package com.example.mealmate.ui.meal_details.presenter;

import com.example.mealmate.data.meals.models.Meal;

import java.util.Date;

public interface MealDetailsPresenter {
    void getMealDetails(Meal meal);
    void onFavoriteClicked(Meal meal);
    void onAddToPlanClicked(Meal meal);
    void addToFavorites(Meal meal);
    void addToPlan(Meal meal, Date date, String mealType);
    void removeFromFavorites(Meal meal);
    void logout();
    void onDestroy();
}