package com.example.mealmate.ui.meals.presenter;

import android.content.Context;

import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.meals.view.MealsView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsPresenterImp implements MealsPresenter {

    private final MealsView view;
    private final MealRepository mealRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MealsPresenterImp(MealsView view, Context context) {
        this.view = view;
        this.mealRepository = new MealRepository(context);
    }

    @Override
    public void getMealsByCategory(String categoryType) {
        if (view != null) view.showLoading();

        Single<List<Meal>> networkMealsSingle = mealRepository.filterBy("Category", categoryType)
                .subscribeOn(Schedulers.io())
                .flattenAsObservable(meals -> meals)
                .concatMapEager(meal ->
                        mealRepository.getMealById(meal.idMeal).toObservable()
                )
                .toList();

        Single<List<String>> favoriteIdsSingle = mealRepository.getFavorites()
                .first(new ArrayList<>())
                .map(meals -> {
                    List<String> ids = new ArrayList<>();
                    for (Meal meal : meals) {
                        ids.add(meal.getId());
                    }
                    return ids;
                })
                .subscribeOn(Schedulers.io());

        compositeDisposable.add(Single.zip(networkMealsSingle, favoriteIdsSingle, (meals, ids) -> {
                    for (Meal meal : meals) {
                        if (ids.contains(meal.getId())) {
                            meal.isFavorite = true;
                        } else {
                            meal.isFavorite = false;
                        }
                    }
                    return meals;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        fullMeals -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showMeals(fullMeals);
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            }
                        }
                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}