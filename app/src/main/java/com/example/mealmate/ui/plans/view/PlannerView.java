package com.example.mealmate.ui.plans.view;

import com.example.mealmate.data.meals.models.MealPlannerItem;
import java.util.List;

public interface PlannerView {
    void showLoading();
    void hideLoading();
    void showPlans(List<MealPlannerItem> items);
    void showDayMealCount(int count);
    void showError(String message);
}
