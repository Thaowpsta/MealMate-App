package com.example.mealmate.ui.search.presenter;

import java.util.List;
import java.util.Map;

public interface SearchPresenter {
    void getAllMeals();
    void searchMeals(String query, Map<String, List<String>> filters);
    void loadCategories();
    void loadAreas();
    void loadIngredients();
    void onDestroy();
}