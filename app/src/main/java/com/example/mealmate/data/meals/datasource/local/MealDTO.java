package com.example.mealmate.data.meals.datasource.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.example.mealmate.data.meals.models.Meal;

@Entity(tableName = "favorite_meals", primaryKeys = {"idMeal", "userId"})
public class MealDTO {

    @NonNull
    public String idMeal;
    @NonNull
    public String userId;

    public String strMeal;
    public String strCategory;
    public String strArea;
    public String strMealThumb;
    public String strInstructions;
    public String strYoutube;
    public boolean isFavorite = false;

    public MealDTO() {}

    public static MealDTO fromMeal(Meal meal, String userId) {
        MealDTO entity = new MealDTO();
        if (meal.getId() != null) entity.idMeal = meal.getId();
        entity.userId = userId;
        entity.strMeal = meal.strMeal;
        entity.strCategory = meal.strCategory;
        entity.strArea = meal.strArea;
        entity.strMealThumb = meal.strMealThumb;
        entity.strInstructions = meal.strInstructions;
        entity.strYoutube = meal.strYoutube;
        entity.isFavorite = meal.isFavorite;
        return entity;
    }

    public Meal toMeal() {
        Meal meal = new Meal();
        meal.idMeal = this.idMeal;
        meal.strMeal = this.strMeal;
        meal.strCategory = this.strCategory;
        meal.strArea = this.strArea;
        meal.strMealThumb = this.strMealThumb;
        meal.strInstructions = this.strInstructions;
        meal.strYoutube = this.strYoutube;
        meal.isFavorite = this.isFavorite;
        return meal;
    }
}