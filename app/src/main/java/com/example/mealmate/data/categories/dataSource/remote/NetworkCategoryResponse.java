package com.example.mealmate.data.categories.dataSource.remote;

import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.model.Meal;

import java.util.List;

public interface NetworkCategoryResponse {

    void onSuccess(List<Category> categories);
    void onFailure(String msg);

}
