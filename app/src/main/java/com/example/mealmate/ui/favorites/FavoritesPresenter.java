package com.example.mealmate.ui.favorites;

import com.example.mealmate.data.remote.MealService;
import com.example.mealmate.data.remote.RetrofitClient;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritesPresenter implements FavoritesContract.Presenter{

    private final FavoritesContract.View view;
    private final MealService mealService;
    private final CompositeDisposable compositeDisposable;

    public FavoritesPresenter(FavoritesContract.View view) {
        this.view = view;
        mealService = RetrofitClient.getClient().create((MealService.class));
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getFavorites() {
        compositeDisposable.add(
                mealService.getRandomMeal().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        mealResponse -> {
                            if (mealResponse.meals != null && !mealResponse.meals.isEmpty())
                                view.showFavorites(mealResponse.meals);
                        },
                        error -> view.showError("No categories found")
                )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}
