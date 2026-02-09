package com.example.mealmate.ui.search.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

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
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImp implements SearchPresenter {

    private final SearchView view;
    private final MealRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<Meal> cachedFilteredMeals = new ArrayList<>();
    private final Context context;

    public SearchPresenterImp(SearchView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
        this.context = context;
    }

    @Override
    public void getAllMeals() {
        cachedFilteredMeals.clear();

        view.showLoading();
        compositeDisposable.add(repository.searchMeals("a")
                .flatMap(this::mapFavorites) // Check favorites
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
                .flatMap(this::mapFavorites) // Check favorites
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> view.showSearchResults(meals),
                        error -> view.showError(error.getMessage())
                ));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }

    @Override
    public void onSearchBarClicked() {
        if (!isNetworkAvailable()) {
            view.showConnectionError();
        }
    }

    @Override
    public void searchMeals(String query, Map<String, List<String>> filters) {
        if (filters == null || filters.isEmpty()) {
            if (query == null || query.trim().isEmpty()) {
                getAllMeals();
                return;
            }
            view.showLoading();
            compositeDisposable.add(repository.searchMeals(query)
                    .flatMap(this::mapFavorites)
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

        view.showLoading();

        List<Single<List<Meal>>> typeRequests = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
            String type = entry.getKey();
            List<String> values = entry.getValue();
            if (values.isEmpty()) continue;

            List<Single<List<Meal>>> valueRequests = new ArrayList<>();
            for (String val : values) {
                valueRequests.add(repository.filterBy(type, val));
            }

            Single<List<Meal>> typeUnion = Single.merge(valueRequests)
                    .collectInto(new ArrayList<Meal>(), List::addAll)
                    .map(list -> {
                        Map<String, Meal> uniqueMap = new HashMap<>();
                        for (Meal m : list) uniqueMap.put(m.getId(), m);
                        return new ArrayList<>(uniqueMap.values());
                    });

            typeRequests.add(typeUnion);
        }

        if (typeRequests.isEmpty()) {
            searchMeals(query, null);
            return;
        }

        compositeDisposable.add(Single.zip(typeRequests, objects -> {
                    if (objects.length == 0) return new ArrayList<Meal>();

                    List<Meal> result = new ArrayList<>((List<Meal>) objects[0]);

                    for (int i = 1; i < objects.length; i++) {
                        List<Meal> nextList = (List<Meal>) objects[i];
                        List<String> nextIds = nextList.stream().map(Meal::getId).collect(Collectors.toList());
                        result.removeIf(m -> !nextIds.contains(m.getId()));
                    }
                    return result;
                })
                .flatMap(this::mapFavorites)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(meals -> {
                    filterLocalList(query, meals);
                }, error -> {
                    view.hideLoading();
                    view.showError(error.getMessage());
                }));
    }

    private void filterLocalList(String query, List<Meal> meals) {
        view.hideLoading();
        if (meals.isEmpty()) {
            view.showEmptyState();
            return;
        }
        if (query == null || query.trim().isEmpty()) {
            view.showSearchResults(meals);
        } else {
            List<Meal> filtered = meals.stream()
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
                    view.showFilterOptions(uiModels, true);
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
                        String countryCode = getCountryCode(m.strArea);
                        String flagUrl = null;

                        if (countryCode != null) {
                            flagUrl = "https://flagcdn.com/w320/" + countryCode + ".png";
                        }

                        uiModels.add(new FilterUIModel(m.strArea, flagUrl));
                    }
                    view.showFilterOptions(uiModels, true);
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

    private String getCountryCode(String areaName) {
        if (areaName == null) return null;

        switch (areaName) {
            case "Algerian": return "dz";
            case "American": return "us";
            case "Argentinian": return "ar";
            case "Australian": return "au";
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
            case "Saudi Arabian": return "sa";
            case "Slovakian": return "sk";
            case "Spanish": return "es";
            case "Syrian": return "sy";
            case "Thai": return "th";
            case "Tunisian": return "tn";
            case "Turkish": return "tr";
            case "Ukrainian": return "ua";
            case "Uruguayan": return "uy";
            case "Vietnamese": return "vn";
            case "Venezulan": return "ve";
            default: return null;
        }
    }

    private Single<List<Meal>> mapFavorites(List<Meal> meals) {
        return repository.getFavorites()
                .first(new ArrayList<>())
                .map(favMeals -> {
                    List<String> favIds = new ArrayList<>();
                    for(Meal m : favMeals) {
                        favIds.add(m.getId());
                    }

                    for (Meal meal : meals) {
                        meal.isFavorite = favIds.contains(meal.getId());
                    }
                    return meals;
                });
    }
}