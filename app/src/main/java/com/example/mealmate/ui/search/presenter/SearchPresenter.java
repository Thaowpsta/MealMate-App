package com.example.mealmate.ui.search.presenter;

import java.util.List;

public interface SearchPresenter {
    void getAllMeals();
    void searchMeals(String query, String filterType, List<String> filterValues);
    void loadCategories();
    void loadAreas();
    void loadIngredients();
    void onDestroy();
}