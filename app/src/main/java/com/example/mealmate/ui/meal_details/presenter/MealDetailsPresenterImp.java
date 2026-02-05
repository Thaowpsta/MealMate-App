package com.example.mealmate.ui.meal_details.presenter;

import android.content.Context;
import android.util.Log;

import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.meal_details.view.MealDetailsView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MealDetailsPresenterImp implements MealDetailsPresenter {

    private MealDetailsView view;
    private final MealRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public MealDetailsPresenterImp(MealDetailsView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
    }

    @Override
    public void getMealDetails(Meal meal) {
        if (view != null && meal != null) {
            disposable.add(repository.isFavorite(meal.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isFav -> {
                        meal.isFavorite = isFav;
                        if (view != null) {
                            view.showMeal(meal);
                        }
                    }, throwable -> {
                        if (view != null) {
                            view.showMeal(meal);
                        }
                    }));
        } else if (view != null) {
            view.showError("Could not load meal details");
        }
    }

    @Override
    public void addToFavorites(Meal meal) {
        if (meal == null) return;
        disposable.add(repository.addFavorite(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (view != null) {
                        meal.isFavorite = true;
                        view.showMeal(meal);
                    }
                }, throwable -> {
                    if (view != null) view.showError(throwable.getMessage());
                }));
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        if (meal == null) return;
        disposable.add(repository.removeFavorite(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (view != null) {
                        meal.isFavorite = false;
                        view.showMeal(meal);
                    }
                }, throwable -> {
                    if (view != null) view.showError(throwable.getMessage());
                }));
    }

    @Override
    public void onDestroy() {
        view = null;
        disposable.clear();
    }
}
