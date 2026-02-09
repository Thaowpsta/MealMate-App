package com.example.mealmate.data.meals.datasource.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

@Dao
public interface MealDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFavorite(MealDTO meal);

    @Query("DELETE FROM favorite_meals WHERE idMeal = :id AND userId = :userId")
    Completable removeFavorite(String id, String userId);

    @Query("SELECT * FROM favorite_meals WHERE isFavorite = 1 AND userId = :userId")
    Flowable<List<MealDTO>> getFavoriteMeals(String userId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE idMeal = :id AND isFavorite = 1 AND userId = :userId)")
    Single<Boolean> isFavorite(String id, String userId);

    @Query("SELECT * FROM meal_plans WHERE date = :date AND userId = :userId")
    Flowable<List<PlannedMealDTO>> getPlansByDate(String date, String userId);

    @Query("SELECT COUNT(*) FROM meal_plans WHERE userId = :userId")
    Flowable<Integer> getPlansCount(String userId);

    @Query("SELECT * FROM meal_plans WHERE date < :todayDate AND userId = :userId")
    Single<List<PlannedMealDTO>> getPastPlans(String todayDate, String userId);

    @Query("DELETE FROM meal_plans WHERE date < :todayDate AND userId = :userId")
    Completable deletePastPlans(String todayDate, String userId);

    @Query("SELECT * FROM meal_plans WHERE userId = :userId")
    Flowable<List<PlannedMealDTO>> getAllPlans(String userId);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMealSync(MealDTO meal);

    @Insert
    void insertPlanSync(PlannedMealDTO plan);

    @Delete
    void deletePlanSync(PlannedMealDTO plan);

    @Query("SELECT * FROM meal_plans WHERE date = :date AND mealType = :type AND userId = :userId")
    PlannedMealDTO getPlanByDateAndTypeSync(String date, String type, String userId);

    @Query("DELETE FROM meal_plans WHERE date = :date AND mealType = :type AND userId = :userId")
    Completable deletePlan(String date, String type, String userId);
}