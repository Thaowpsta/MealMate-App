package com.example.mealmate.ui.favorites.presenter;

import com.example.mealmate.data.meals.datasource.remote.MealRemoteDataSource;
import com.example.mealmate.data.meals.datasource.remote.NetworkMealResponse;
import com.example.mealmate.data.meals.model.Meal;
import com.example.mealmate.data.network.MealService;
import com.example.mealmate.data.network.RetrofitClient;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.favorites.view.FavoriteView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritesPresenterImp implements FavoritePresenter {

    private final FavoriteView view;
    private final MealRepository repository;

    public FavoritesPresenterImp(FavoriteView view) {
        this.view = view;
        repository = new MealRepository();
    }

    @Override
    public void getFavorites() {
        repository.getRandomMeal(new NetworkMealResponse() {
            @Override
            public void onSuccess(List<Meal> meals) {
                view.hideLoading();
                view.showFavorites(meals);
            }

            @Override
            public void onFailure(String msg) {
                view.hideLoading();
                view.showError("No categories found");
            }
        });
    }

    @Override
    public void onDestroy() {
    }
}
