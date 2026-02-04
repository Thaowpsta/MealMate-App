package com.example.mealmate.ui.categories.view;

import com.example.mealmate.data.categories.model.Category;

import java.util.List;

public interface CategoriesView {
        void showLoading();
        void hideLoading();
        void showCategories(List<Category> categories);
        void showError(String message);
    }
