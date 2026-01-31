package com.example.mealmate.ui.meal_details;

import com.example.mealmate.data.models.Meal;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private MealDetailsContract.View view;

    public MealDetailsPresenter(MealDetailsContract.View view) {
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