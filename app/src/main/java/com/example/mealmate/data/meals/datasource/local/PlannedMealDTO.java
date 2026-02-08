package com.example.mealmate.data.meals.datasource.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_plans")
public class PlannedMealDTO {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId;
    public String mealId;

    public String date;
    public String dayOfWeek;
    public String mealType;
    public String mealName;
    public String mealThumb;
}