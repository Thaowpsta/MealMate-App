package com.example.mealmate.data.repositories;

import com.example.mealmate.data.categories.dataSource.remote.CategoryRemoteDataSource;
import com.example.mealmate.data.categories.dataSource.remote.NetworkCategoryResponse;
import com.example.mealmate.data.meals.datasource.remote.MealRemoteDataSource;
import com.example.mealmate.data.meals.datasource.remote.NetworkMealResponse;

public class MealRepository {

    MealRemoteDataSource mealRemoteDataSource;
    CategoryRemoteDataSource categoryRemoteDataSource;

    public MealRepository() {
        mealRemoteDataSource = new MealRemoteDataSource();
        categoryRemoteDataSource = new CategoryRemoteDataSource();
    }

    public void getRandomMeal(NetworkMealResponse response){
        mealRemoteDataSource.getRandomMealService(response);
    }

    public void getCategories(NetworkCategoryResponse response){
        categoryRemoteDataSource.getCategoriesService(response);
    }
}
