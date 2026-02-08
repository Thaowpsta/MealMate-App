package com.example.mealmate.ui.home.view;

import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.datasource.local.PlannedMealDTO;
import com.example.mealmate.data.meals.models.Meal;

import java.util.List;

public interface HomeView {
    void showLoading();

    void hideLoading();

    void showMeal(Meal meal);

    void navigateToMealDetails(Meal meal);

    void showCategories(List<Category> categories);

    void showFavoritesCount(int count);

    void onPlanAddedSuccess();

    void onPlanAddedError(String error);

    void showPlansCount(int count);

    void showTodaysPlan(PlannedMealDTO plan);

    void showError(String message);

    void navigateToLogin();

    void showConnectionError();

    void showGuestLoginDialog();

    void showWeekCalendarDialog();
}