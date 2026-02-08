package com.example.mealmate.data.meals.datasource.remote;

import com.example.mealmate.data.meals.models.Ingredient;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.meals.models.MealResponse;
import com.example.mealmate.data.network.MealService;
import com.example.mealmate.data.network.RetrofitClient;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MealRemoteDataSource {

    private final MealService mealService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MealRemoteDataSource() {
        mealService = RetrofitClient.getInstance().getMealService();
    }

    public Single<List<Meal>> getRandomMealService() {
        return mealService.getRandomMeal()
                .map(response -> response.meals);
    }

    public Single<Meal> getMealByIdService(String id) {
        return mealService.getMealById(id)
                .map(response -> {
                    if (response.meals != null && !response.meals.isEmpty()) {
                        return response.meals.get(0);
                    } else {
                        throw new Exception("Meal not found");
                    }
                });
    }

    public Single<List<Meal>> searchMeals(String query) {
        return mealService.searchMeals(query)
                .map(response -> response.meals);
    }

    public Single<MealResponse> filterByCategory(String category) {
        return mealService.filterByCategory(category);
    }

    public Single<MealResponse> filterByArea(String area) {
        return mealService.filterByArea(area);
    }

    public Single<MealResponse> filterByIngredient(String ingredient) {
        return mealService.filterByIngredient(ingredient);
    }

    public Single<List<Meal>> getAreas() {
        return mealService.getAreas()
                .map(response -> response.meals);
    }

    public Single<List<Ingredient>> getIngredients() {
        return mealService.getIngredients()
                .map(response -> response.meals);
    }

    public void clearDisposables() {
        compositeDisposable.clear();
    }
}