package com.example.mealmate.ui.search.presenter;

import android.content.Context;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.models.FilterUIModel;
import com.example.mealmate.data.meals.models.Ingredient;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.search.view.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SearchPresenterImp implements SearchPresenter {

    private final SearchView view;
    private final MealRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Cache to hold the meals of the currently selected category/area/ingredient
    private List<Meal> cachedFilteredMeals = new ArrayList<>();
    private String lastFilterType = "";
    private String lastFilterQuery = "";

    public SearchPresenterImp(SearchView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
    }

    // ... [getAllMeals, loadDefaultMeals, searchMeals, filterLocalList methods remain unchanged] ...
    @Override
    public void getAllMeals() {
        // Search with single letter to get a broad set of meals initially
        lastFilterType = "";
        lastFilterQuery = "";
        cachedFilteredMeals.clear();

        view.showLoading();
        compositeDisposable.add(repository.searchMeals("a")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            view.hideLoading();
                            if (meals != null && !meals.isEmpty()) {
                                view.showSearchResults(meals);
                            } else {
                                loadDefaultMeals();
                            }
                        },
                        error -> {
                            view.hideLoading();
                            loadDefaultMeals();
                        }
                ));
    }

    private void loadDefaultMeals() {
        compositeDisposable.add(repository.getCategories()
                .flatMap(categories -> {
                    if (categories != null && !categories.isEmpty()) {
                        return repository.filterBy("Category", categories.get(0).getStrCategory());
                    }
                    return Single.just(new ArrayList<Meal>());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> view.showSearchResults(meals),
                        error -> view.showError(error.getMessage())
                ));
    }

    @Override
    public void searchMeals(String query, String filterType, List<String> filterValues) {
        if (filterType == null || filterValues == null || filterValues.isEmpty()) {
            if (query == null || query.trim().isEmpty()) return;
            view.showLoading();
            compositeDisposable.add(repository.searchMeals(query)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            meals -> {
                                view.hideLoading();
                                view.showSearchResults(meals);
                            },
                            error -> {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            }
                    ));
            return;
        }

        String currentFilterQuery;
        if (filterValues.size() > 1) {
            currentFilterQuery = String.join(",", filterValues);
        } else {
            currentFilterQuery = filterValues.get(0);
        }

        if (filterType.equals(lastFilterType) && currentFilterQuery.equals(lastFilterQuery) && !cachedFilteredMeals.isEmpty()) {
            filterLocalList(query);
        } else {
            view.showLoading();
            compositeDisposable.add(repository.filterBy(filterType, currentFilterQuery)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(meals -> {
                        cachedFilteredMeals = meals != null ? meals : new ArrayList<>();
                        lastFilterType = filterType;
                        lastFilterQuery = currentFilterQuery;
                        filterLocalList(query);
                    }, error -> {
                        view.hideLoading();
                        view.showError(error.getMessage());
                    }));
        }
    }

    private void filterLocalList(String query) {
        view.hideLoading();
        if (cachedFilteredMeals.isEmpty()) {
            view.showEmptyState();
            return;
        }
        if (query == null || query.trim().isEmpty()) {
            view.showSearchResults(cachedFilteredMeals);
        } else {
            List<Meal> filtered = cachedFilteredMeals.stream()
                    .filter(m -> m.strMeal.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            view.showSearchResults(filtered);
        }
    }

    @Override
    public void loadCategories() {
        view.showLoading();
        compositeDisposable.add(repository.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    List<FilterUIModel> uiModels = new ArrayList<>();
                    for (Category c : categories) {
                        uiModels.add(new FilterUIModel(c.getStrCategory(), c.strCategoryThumb));
                    }
                    view.showFilterOptions(uiModels, false);
                    view.hideLoading();
                }, error -> view.showError(error.getMessage())));
    }

    @Override
    public void loadAreas() {
        view.showLoading();
        compositeDisposable.add(repository.getAreas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(meals -> {
                    List<FilterUIModel> uiModels = new ArrayList<>();
                    for (Meal m : meals) {
                        // 1. Get the country code for the area name
                        String countryCode = getCountryCode(m.strArea);
                        String flagUrl = null;

                        // 2. Construct the flag URL (using flagcdn)
                        if (countryCode != null) {
                            flagUrl = "https://flagcdn.com/w320/" + countryCode + ".png";
                        }

                        uiModels.add(new FilterUIModel(m.strArea, flagUrl));
                    }
                    view.showFilterOptions(uiModels, false);
                    view.hideLoading();
                }, error -> view.showError(error.getMessage())));
    }

    @Override
    public void loadIngredients() {
        view.showLoading();
        compositeDisposable.add(repository.getIngredients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ingredients -> {
                    List<FilterUIModel> uiModels = new ArrayList<>();
                    for (Ingredient i : ingredients) {
                        String thumb = "https://www.themealdb.com/images/ingredients/" + i.strIngredient + "-Small.png";
                        uiModels.add(new FilterUIModel(i.strIngredient, thumb));
                    }
                    view.showFilterOptions(uiModels, true);
                    view.hideLoading();
                }, error -> view.showError(error.getMessage())));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }

    // Helper method to map Area names to ISO 2-letter country codes
    private String getCountryCode(String areaName) {
        if (areaName == null) return null;

        switch (areaName) {
            case "Algerian": return "dz";      // Added
            case "American": return "us";
            case "Argentinian": return "ar";   // Added
            case "Australian": return "au";    // Added
            case "British": return "gb";
            case "Canadian": return "ca";
            case "Chinese": return "cn";
            case "Croatian": return "hr";
            case "Dutch": return "nl";
            case "Egyptian": return "eg";
            case "Filipino": return "ph";
            case "French": return "fr";
            case "Greek": return "gr";
            case "Indian": return "in";
            case "Irish": return "ie";
            case "Italian": return "it";
            case "Jamaican": return "jm";
            case "Japanese": return "jp";
            case "Kenyan": return "ke";
            case "Malaysian": return "my";
            case "Mexican": return "mx";
            case "Moroccan": return "ma";
            case "Norwegian": return "no";
            case "Polish": return "pl";
            case "Portuguese": return "pt";
            case "Russian": return "ru";
            case "Saudi Arabian": return "sa"; // Added
            case "Slovakian": return "sk";     // Added
            case "Spanish": return "es";
            case "Syrian": return "sy";        // Added
            case "Thai": return "th";
            case "Tunisian": return "tn";
            case "Turkish": return "tr";
            case "Ukrainian": return "ua";
            case "Uruguayan": return "uy";     // Added
            case "Vietnamese": return "vn";
            case "Venezulan": return "ve";    // Added
            default: return null;
        }
    }
}