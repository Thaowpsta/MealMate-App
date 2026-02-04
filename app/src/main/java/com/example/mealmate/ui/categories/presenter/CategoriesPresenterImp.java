package com.example.mealmate.ui.categories.presenter;

import com.example.mealmate.data.categories.dataSource.remote.CategoryRemoteDataSource;
import com.example.mealmate.data.categories.dataSource.remote.NetworkCategoryResponse;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.network.MealService;
import com.example.mealmate.data.network.RetrofitClient;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.categories.view.CategoriesView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoriesPresenterImp implements CategoriesPresenter {

    private final CategoriesView view;
    private final MealRepository repository;

    public CategoriesPresenterImp(CategoriesView view) {
        this.view = view;
        repository = new MealRepository();
    }


    public void getCategories() {
        repository.getCategories(new NetworkCategoryResponse() {
            @Override
            public void onSuccess(List<Category> categories) {
                view.hideLoading();
                view.showCategories(categories);
            }

            @Override
            public void onFailure(String msg) {
                view.hideLoading();
                view.showError(msg);
            }
        });
    }

    @Override
    public void onDestroy() {
    }

}
