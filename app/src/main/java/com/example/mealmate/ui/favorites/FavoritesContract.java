package com.example.mealmate.ui.favorites;

import com.example.mealmate.data.models.Meal;

import java.util.List;

public interface FavoritesContract {

    interface View{
        void showLoading();
        void hideLoading();
        void showFavorites(List<Meal> meals);
        void showError(String message);
    }

    interface Presenter{
        void getFavorites();
        void onDestroy();
    }
}
