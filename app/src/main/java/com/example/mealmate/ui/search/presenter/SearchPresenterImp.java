package com.example.mealmate.ui.search.presenter;

import android.content.Context;

import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.search.view.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImp implements SearchPresenter {

    private final SearchView view;
    private final MealRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    // Keep track of the current search to cancel it if a new one starts
    private Disposable searchDisposable;

    public SearchPresenterImp(SearchView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
    }

    @Override
    public void getAllMeals() {
        List<String> defaultType = new ArrayList<>();
        defaultType.add("Name");
        performSearch("", defaultType);
    }

    @Override
    public void searchMeals(String query, List<String> searchTypes) {
        if (view == null) return;

        if (query == null || query.trim().isEmpty()) {
            getAllMeals();
            return;
        }

        if (searchTypes == null || searchTypes.isEmpty()) {
            searchTypes = new ArrayList<>();
            searchTypes.add("Name");
        }

        performSearch(query, searchTypes);
    }

    private void performSearch(String query, List<String> searchTypes) {
        // Cancel previous search if it exists
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }

        view.showLoading();

        List<Observable<List<Meal>>> observables = new ArrayList<>();

        for (String type : searchTypes) {
            if ("Name".equalsIgnoreCase(type)) {
                observables.add(repository.searchMeals(query).toObservable().onErrorReturnItem(new ArrayList<>()));
            } else {
                observables.add(repository.filterBy(type, query).toObservable().onErrorReturnItem(new ArrayList<>()));
            }
        }

        Observable<List<Meal>> mergedSearch = Observable.merge(observables)
                .collectInto(new HashMap<String, Meal>(), (map, list) -> {
                    for (Meal meal : list) {
                        map.put(meal.getId(), meal);
                    }
                })
                .map(map -> (List<Meal>) new ArrayList<>(map.values()))
                .toObservable();

        searchDisposable = mergedSearch.flatMapSingle(meals -> {
                    if (meals.isEmpty()) return Single.just(meals);
                    return repository.getFavorites().first(new ArrayList<>())
                            .map(favorites -> {
                                List<String> favIds = new ArrayList<>();
                                for (Meal fav : favorites) favIds.add(fav.getId());
                                for (Meal meal : meals) meal.isFavorite = favIds.contains(meal.getId());
                                return meals;
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            if (view != null) {
                                view.hideLoading();
                                if (meals.isEmpty()) {
                                    view.showEmptyState();
                                } else {
                                    view.showSearchResults(meals);
                                }
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            }
                        }
                );

        compositeDisposable.add(searchDisposable);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}