package com.example.mealmate.ui.categories;

import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.mealmate.data.models.Category;
import com.example.mealmate.data.remote.MealService;
import com.example.mealmate.data.remote.RetrofitClient;
import com.example.mealmate.ui.home.HomeContract;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoriesPresenter  implements CategoriesContract.Presenter{

    private final CategoriesContract.View view;
    private final MealService mealService;
    private final CompositeDisposable compositeDisposable;

    public CategoriesPresenter(CategoriesContract.View view) {
        this.view = view;
        this.mealService = RetrofitClient.getClient().create(MealService.class);
        this.compositeDisposable = new CompositeDisposable();
    }


    public void getCategories() {
        compositeDisposable.add(
                mealService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    if (response.categories != null && !response.categories.isEmpty()) {
                                        view.showCategories(response.categories);
                                    }
                                },
                                error -> {
                                    view.showError("No categories found");
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }

}
