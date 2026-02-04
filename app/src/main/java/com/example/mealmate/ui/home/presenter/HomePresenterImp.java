package com.example.mealmate.ui.home.presenter;

import com.example.mealmate.data.categories.dataSource.remote.NetworkCategoryResponse;
import com.example.mealmate.data.meals.datasource.remote.NetworkMealResponse;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.home.view.HomeView;

import java.util.ArrayList;
import java.util.List;

public class HomePresenterImp implements HomePresenter {

    private final HomeView view;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public HomePresenterImp(HomeView view, MealRepository mealRepository, UserRepository userRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void getRandomMeal() {
        mealRepository.getRandomMeal(new NetworkMealResponse() {
            @Override
            public void onSuccess(List<Meal> meals) {
                if (view != null) {
                    view.hideLoading();
                    if (meals != null && !meals.isEmpty()) {
                        view.showMeal(meals.get(0));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                if (view != null) {
                    view.hideLoading();
                    view.showError("No meals found");
                }
            }
        });
    }

    @Override
    public void getCategories() {
        mealRepository.getCategories(new NetworkCategoryResponse() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (view != null) {
                    view.hideLoading();
                    int limit = Math.min(categories.size(), 5);
                    List<Category> firstFive = new ArrayList<>(categories.subList(0, limit));
                    view.showCategories(firstFive);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (view != null) {
                    view.showError("No categories found");
                }
            }
        });
    }

    @Override
    public void logout() {
        userRepository.logout();
        if (view != null) {
            view.navigateToLogin();
        }
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
