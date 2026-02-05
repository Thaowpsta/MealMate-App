package com.example.mealmate.ui.favorites.presenter;

import android.content.Context;

import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.favorites.view.FavoriteView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritesPresenterImp implements FavoritePresenter {

    private final FavoriteView view;
    private final MealRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FavoritesPresenterImp(FavoriteView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
    }

    @Override
    public void getFavorites() {
        view.showLoading();
        compositeDisposable.add(repository.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            view.hideLoading();
                            view.showFavorites(meals);
                        },
                        error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        }
                ));
    }

    @Override
    public void removeFavorite(Meal meal) {
        compositeDisposable.add(repository.removeFavorite(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::getFavorites,
                        error -> view.showError(error.getMessage())
                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}
