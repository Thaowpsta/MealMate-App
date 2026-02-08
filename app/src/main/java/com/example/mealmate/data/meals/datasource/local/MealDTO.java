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

    // Ingredients
    public String strIngredient1;
    public String strIngredient2;
    public String strIngredient3;
    public String strIngredient4;
    public String strIngredient5;
    public String strIngredient6;
    public String strIngredient7;
    public String strIngredient8;
    public String strIngredient9;
    public String strIngredient10;
    public String strIngredient11;
    public String strIngredient12;
    public String strIngredient13;
    public String strIngredient14;
    public String strIngredient15;
    public String strIngredient16;
    public String strIngredient17;
    public String strIngredient18;
    public String strIngredient19;
    public String strIngredient20;

    // Measures
    public String strMeasure1;
    public String strMeasure2;
    public String strMeasure3;
    public String strMeasure4;
    public String strMeasure5;
    public String strMeasure6;
    public String strMeasure7;
    public String strMeasure8;
    public String strMeasure9;
    public String strMeasure10;
    public String strMeasure11;
    public String strMeasure12;
    public String strMeasure13;
    public String strMeasure14;
    public String strMeasure15;
    public String strMeasure16;
    public String strMeasure17;
    public String strMeasure18;
    public String strMeasure19;
    public String strMeasure20;

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

        entity.strIngredient1 = meal.strIngredient1;
        entity.strIngredient2 = meal.strIngredient2;
        entity.strIngredient3 = meal.strIngredient3;
        entity.strIngredient4 = meal.strIngredient4;
        entity.strIngredient5 = meal.strIngredient5;
        entity.strIngredient6 = meal.strIngredient6;
        entity.strIngredient7 = meal.strIngredient7;
        entity.strIngredient8 = meal.strIngredient8;
        entity.strIngredient9 = meal.strIngredient9;
        entity.strIngredient10 = meal.strIngredient10;
        entity.strIngredient11 = meal.strIngredient11;
        entity.strIngredient12 = meal.strIngredient12;
        entity.strIngredient13 = meal.strIngredient13;
        entity.strIngredient14 = meal.strIngredient14;
        entity.strIngredient15 = meal.strIngredient15;
        entity.strIngredient16 = meal.strIngredient16;
        entity.strIngredient17 = meal.strIngredient17;
        entity.strIngredient18 = meal.strIngredient18;
        entity.strIngredient19 = meal.strIngredient19;
        entity.strIngredient20 = meal.strIngredient20;

        entity.strMeasure1 = meal.strMeasure1;
        entity.strMeasure2 = meal.strMeasure2;
        entity.strMeasure3 = meal.strMeasure3;
        entity.strMeasure4 = meal.strMeasure4;
        entity.strMeasure5 = meal.strMeasure5;
        entity.strMeasure6 = meal.strMeasure6;
        entity.strMeasure7 = meal.strMeasure7;
        entity.strMeasure8 = meal.strMeasure8;
        entity.strMeasure9 = meal.strMeasure9;
        entity.strMeasure10 = meal.strMeasure10;
        entity.strMeasure11 = meal.strMeasure11;
        entity.strMeasure12 = meal.strMeasure12;
        entity.strMeasure13 = meal.strMeasure13;
        entity.strMeasure14 = meal.strMeasure14;
        entity.strMeasure15 = meal.strMeasure15;
        entity.strMeasure16 = meal.strMeasure16;
        entity.strMeasure17 = meal.strMeasure17;
        entity.strMeasure18 = meal.strMeasure18;
        entity.strMeasure19 = meal.strMeasure19;
        entity.strMeasure20 = meal.strMeasure20;

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

        meal.strIngredient1 = this.strIngredient1;
        meal.strIngredient2 = this.strIngredient2;
        meal.strIngredient3 = this.strIngredient3;
        meal.strIngredient4 = this.strIngredient4;
        meal.strIngredient5 = this.strIngredient5;
        meal.strIngredient6 = this.strIngredient6;
        meal.strIngredient7 = this.strIngredient7;
        meal.strIngredient8 = this.strIngredient8;
        meal.strIngredient9 = this.strIngredient9;
        meal.strIngredient10 = this.strIngredient10;
        meal.strIngredient11 = this.strIngredient11;
        meal.strIngredient12 = this.strIngredient12;
        meal.strIngredient13 = this.strIngredient13;
        meal.strIngredient14 = this.strIngredient14;
        meal.strIngredient15 = this.strIngredient15;
        meal.strIngredient16 = this.strIngredient16;
        meal.strIngredient17 = this.strIngredient17;
        meal.strIngredient18 = this.strIngredient18;
        meal.strIngredient19 = this.strIngredient19;
        meal.strIngredient20 = this.strIngredient20;

        meal.strMeasure1 = this.strMeasure1;
        meal.strMeasure2 = this.strMeasure2;
        meal.strMeasure3 = this.strMeasure3;
        meal.strMeasure4 = this.strMeasure4;
        meal.strMeasure5 = this.strMeasure5;
        meal.strMeasure6 = this.strMeasure6;
        meal.strMeasure7 = this.strMeasure7;
        meal.strMeasure8 = this.strMeasure8;
        meal.strMeasure9 = this.strMeasure9;
        meal.strMeasure10 = this.strMeasure10;
        meal.strMeasure11 = this.strMeasure11;
        meal.strMeasure12 = this.strMeasure12;
        meal.strMeasure13 = this.strMeasure13;
        meal.strMeasure14 = this.strMeasure14;
        meal.strMeasure15 = this.strMeasure15;
        meal.strMeasure16 = this.strMeasure16;
        meal.strMeasure17 = this.strMeasure17;
        meal.strMeasure18 = this.strMeasure18;
        meal.strMeasure19 = this.strMeasure19;
        meal.strMeasure20 = this.strMeasure20;

        return meal;
    }
}