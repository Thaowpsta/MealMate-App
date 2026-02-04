package com.example.mealmate.ui.meal_details.presenter;

import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.meal_details.view.MealDetailsView;

public class MealDetailsPresenterImp implements MealDetailsPresenter {

    private MealDetailsView view;

    public MealDetailsPresenterImp(MealDetailsView view) {
        this.view = view;
    }

    @Override
    public void getMealDetails(Meal meal) {
        if (view != null && meal != null) {
            view.showMeal(meal);
        } else if (view != null) {
            view.showError("Could not load meal details");
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}