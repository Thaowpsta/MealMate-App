package com.example.mealmate.data.meals.datasource.remote;

import com.example.mealmate.data.meals.model.Meal;

import java.util.List;

public interface NetworkMealResponse {
    void onSuccess(List<Meal> meals);
    void onFailure(String msg);
}
