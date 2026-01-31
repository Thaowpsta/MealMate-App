package com.example.mealmate.ui.home;

import com.example.mealmate.data.models.Meal;
import com.example.mealmate.data.remote.MealService;
import com.example.mealmate.data.remote.RetrofitClient;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;
    private MealService mealService;
    private CompositeDisposable compositeDisposable;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
        this.mealService = RetrofitClient.getClient().create(MealService.class);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getRandomMeal() {
        view.showLoading();
        compositeDisposable.add(
                mealService.getRandomMeal()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    view.hideLoading();
                                    if (response.meals != null && !response.meals.isEmpty()) {
                                        view.showMeal(response.meals.get(0));
                                    } else {
                                        view.showError("No meals found");
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError(error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (view != null) {

            view.navigateToMealDetails(meal);
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}