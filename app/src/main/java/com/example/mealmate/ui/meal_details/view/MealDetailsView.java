package com.example.mealmate.ui.meal_details.view;

import com.example.mealmate.data.meals.models.Meal;

public interface MealDetailsView {
    void showMeal(Meal meal);
    void showLoading();
    void hideLoading();
    void showError(String message);
    void onPlanAddedSuccess();
    void onPlanAddedError(String error);
    void showConnectionError();
    void showGuestLoginDialog();
    void showWeekCalendarDialog();
    void navigateToLogin();
}