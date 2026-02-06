package com.example.mealmate.ui.plans.presenter;

import com.example.mealmate.data.meals.models.Meal;
import java.util.Date;

public interface PlannerPresenter {
    void getMealsByDate(Date date);
    void onDestroy();
}
