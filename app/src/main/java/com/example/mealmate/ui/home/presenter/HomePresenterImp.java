package com.example.mealmate.ui.home.presenter;

import com.example.mealmate.data.categories.dataSource.remote.NetworkCategoryResponse;
import com.example.mealmate.data.meals.datasource.remote.NetworkMealResponse;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.model.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.home.view.HomeView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenterImp implements HomePresenter {

    private final HomeView view;
    private final MealRepository repository;

    public HomePresenterImp(HomeView view) {
        this.view = view;
        repository = new MealRepository();
    }

    @Override
    public void getRandomMeal() {

        repository.getRandomMeal(new NetworkMealResponse() {
            @Override
            public void onSuccess(List<Meal> meals) {
                view.hideLoading();
                view.showMeal(meals.get(0));
            }

            @Override
            public void onFailure(String msg) {
                view.hideLoading();
                view.showError("No meals found");

            }
        });
    }

    @Override
    public void getCategories() {

        repository.getCategories(new NetworkCategoryResponse() {
            @Override
            public void onSuccess(List<Category> categories) {
                view.hideLoading();

                int limit = Math.min(categories.size(), 5);
                List<Category> firstFive = new ArrayList<>(categories.subList(0, limit));

                view.showCategories(firstFive);
            }

            @Override
            public void onFailure(String msg) {
                view.showError("No categories found");
            }
        });
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (view != null) {

            view.navigateToMealDetails(meal);
        }
    }

    @Override
    public void onDestroy() {
    }
}