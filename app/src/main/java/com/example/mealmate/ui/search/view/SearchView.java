package com.example.mealmate.ui.search.view;

import com.example.mealmate.data.meals.models.Meal;
import java.util.List;

public interface SearchView {
    void showLoading();
    void hideLoading();
    void showSearchResults(List<Meal> meals);
    void showEmptyState();
    void showError(String message);
}