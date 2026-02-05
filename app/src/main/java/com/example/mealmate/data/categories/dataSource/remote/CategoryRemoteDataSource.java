package com.example.mealmate.data.categories.dataSource.remote;

import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.network.MealService;
import com.example.mealmate.data.network.RetrofitClient;

import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class CategoryRemoteDataSource {
    private final MealService mealService;

    public CategoryRemoteDataSource() {
        mealService = RetrofitClient.getInstance().getMealService();
    }

    public Single<List<Category>> getCategoriesService() {
        return mealService.getCategories()
                .map(response -> response.categories);
    }}
