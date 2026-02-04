package com.example.mealmate.ui.favorites.view;

import com.example.mealmate.data.meals.models.Meal;

import java.util.List;

public interface FavoriteView{
        void showLoading();
        void hideLoading();
        void showFavorites(List<Meal> meals);
        void showError(String message);
    }
