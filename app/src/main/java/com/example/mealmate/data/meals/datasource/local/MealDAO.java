package com.example.mealmate.data.meals.datasource.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

@Dao
public interface MealDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFavorite(MealDTO meal);

    @Query("DELETE FROM favorite_meals WHERE idMeal = :id")
    Completable removeFavorite(String id);

    @Query("SELECT * FROM favorite_meals WHERE isFavorite = 1")
    Flowable<List<MealDTO>> getFavoriteMeals();

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE idMeal = :id AND isFavorite = 1)")
    Single<Boolean> isFavorite(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(MealDTO meal);

    @Query("SELECT * FROM meal_plans WHERE date = :date")
    Flowable<List<PlannedMealDTO>> getPlansByDate(String date);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlan(PlannedMealDTO plan);
    
    @Query("DELETE FROM meal_plans WHERE id = :planId")
    Completable deletePlan(int planId);
}