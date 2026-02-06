package com.example.mealmate.ui.meal_details.presenter;

import android.content.Context;

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
        if (view == null || meal == null) return;

        // Display what we have initially (e.g., image/name from Planner)
        view.showMeal(meal);

        // Check if this is a "partial" meal from Planner (missing instructions)
        if (meal.getStrInstructions() == null || meal.getStrInstructions().isEmpty() || meal.getStrInstructions().startsWith("Planned for")) {
            view.showLoading();
            disposable.add(repository.getMealById(meal.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            fullMeal -> {
                                if (view != null) {
                                    view.hideLoading();
                                    checkFavoriteStatus(fullMeal);
                                }
                            },
                            error -> {
                                if (view != null) {
                                    view.hideLoading();
                                    view.showError("Failed to load full details: " + error.getMessage());
                                }
                            }
                    ));
        } else {
            // It's a full meal object, just check favorite status
            checkFavoriteStatus(meal);
        }
    }

    private void checkFavoriteStatus(Meal meal) {
        if (meal == null || view == null) return;
        disposable.add(repository.isFavorite(meal.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isFav -> {
                            if (view != null) {
                                meal.isFavorite = isFav;
                                view.showMeal(meal);
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.showMeal(meal); // Still show meal even if check fails
                            }
                        }
                ));
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
