package com.example.mealmate.ui.home;

import com.example.mealmate.data.models.Meal;

public interface HomeContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showMeal(Meal meal);
        void navigateToMealDetails(Meal meal);
        void showError(String message);
    }

    interface Presenter {
        void getRandomMeal();
        void onMealClicked(Meal meal);
        void onDestroy();
    }
}