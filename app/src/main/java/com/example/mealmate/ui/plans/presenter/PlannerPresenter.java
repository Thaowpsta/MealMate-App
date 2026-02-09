package com.example.mealmate.ui.plans.presenter;

import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.meals.models.MealPlannerItem;

import java.util.Date;

public interface PlannerPresenter {
    void getMealsByDate(Date date);
    void deletePlan(MealPlannerItem.MealItem item, Date date);
    void onDestroy();
}
