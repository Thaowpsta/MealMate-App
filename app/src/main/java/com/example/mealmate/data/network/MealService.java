package com.example.mealmate.data.network;

import com.example.mealmate.data.categories.model.CategoryResponse;
import com.example.mealmate.data.meals.models.IngredientResponse;
import com.example.mealmate.data.meals.models.MealResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealService {
    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("categories.php")
    Single<CategoryResponse> getCategories();

    @GET("search.php")
    Single<MealResponse> searchMeals(@Query("s") String query);

    @GET("filter.php")
    Single<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealResponse> filterByArea(@Query("a") String area);

    @GET("filter.php")
    Single<MealResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String id);

    @GET("list.php?a=list")
    Single<MealResponse> getAreas();

    @GET("list.php?i=list")
    Single<IngredientResponse> getIngredients();
}