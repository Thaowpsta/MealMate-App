package com.example.mealmate.ui.categories.presenter;

import android.content.Context;

import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.categories.view.CategoriesView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoriesPresenterImp implements CategoriesPresenter {

    private final CategoriesView view;
    private final MealRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CategoriesPresenterImp(CategoriesView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
    }

    @Override
    public void getCategories() {
        if (view != null) view.showLoading();
        compositeDisposable.add(repository.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categories -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showCategories(categories);
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
