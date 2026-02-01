package com.example.mealmate.ui.categories;

import com.example.mealmate.data.models.Category;
import com.example.mealmate.data.models.Meal;

import java.util.List;

public interface CategoriesContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showCategories(List<Category> categories);
        void showError(String message);
    }

    interface Presenter {
        void getCategories();
        void onDestroy();
    }

}
