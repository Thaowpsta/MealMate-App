package com.example.mealmate.ui.meals.view;

import com.example.mealmate.data.meals.models.Meal;

import java.util.List;

public interface MealsView {

    void showLoading();

    void hideLoading();

    void showMeals(List<Meal> meals);

    void showError(String message);

}
