package com.example.mealmate.data.network;

import com.example.mealmate.data.categories.model.CategoryResponse;
import com.example.mealmate.data.meals.models.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MealService {
    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    @GET("categories.php")
    Call<CategoryResponse> getCategories();
}