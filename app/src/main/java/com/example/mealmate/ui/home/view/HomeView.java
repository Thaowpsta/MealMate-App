package com.example.mealmate.ui.home.view;

import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.models.Meal;

import java.util.List;

public interface HomeView {
        void showLoading();
        void hideLoading();
        void showMeal(Meal meal);
        void navigateToMealDetails(Meal meal);
        void showCategories(List<Category> categories);
        void showError(String message);
        void navigateToLogin();
    }
