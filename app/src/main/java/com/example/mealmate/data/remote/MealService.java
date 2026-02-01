package com.example.mealmate.data.remote;

import com.example.mealmate.data.models.CategoryResponse;
import com.example.mealmate.data.models.MealResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface MealService {
    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("categories.php")
    Single<CategoryResponse> getCategories();
}