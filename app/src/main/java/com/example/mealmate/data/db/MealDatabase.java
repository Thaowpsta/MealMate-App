package com.example.mealmate.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.mealmate.data.meals.datasource.local.MealDTO;
import com.example.mealmate.data.meals.datasource.local.MealDAO;
import com.example.mealmate.data.meals.datasource.local.PlannedMealDTO;

@Database(entities = {MealDTO.class, PlannedMealDTO.class}, version = 1)
public abstract class MealDatabase extends RoomDatabase {
    private static volatile MealDatabase INSTANCE;
    public abstract MealDAO mealDao();

    public static MealDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MealDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, "mealmate_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}