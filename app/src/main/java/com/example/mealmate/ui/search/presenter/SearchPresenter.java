package com.example.mealmate.ui.search.presenter;

import java.util.List;

public interface SearchPresenter {
    void getAllMeals();
    void searchMeals(String query, List<String> searchTypes);
    void onDestroy();
}