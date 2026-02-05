package com.example.mealmate.ui.favorites.presenter;

import com.example.mealmate.data.meals.models.Meal;

public interface FavoritePresenter{
        void getFavorites();
        void removeFavorite(Meal meal);
        void onDestroy();
    }
