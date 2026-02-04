package com.example.mealmate.ui.plans.view;

import com.example.mealmate.data.meals.models.MealPlannerItem;
import com.example.mealmate.data.meals.models.MealType;

public interface OnPlannerActionClickListener {
        void onMealClick(MealPlannerItem.MealItem meal);
        void onAddMealClick(MealType mealType);
        void onEmptyDayClick();
    }
