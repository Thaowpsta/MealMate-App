package com.example.mealmate.ui.home.presenter;

import android.content.Context;

import com.example.mealmate.R;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.datasource.local.PlannedMealDTO;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.home.view.HomeView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenterImp implements HomePresenter {

    private final HomeView view;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public HomePresenterImp(HomeView view, Context context) {
        this.view = view;
        mealRepository = new MealRepository(context);
        userRepository = new UserRepository(context);

        mealRepository.deletePastPlans();
    }

    @Override
    public void getRandomMeal() {
        if (view != null) view.showLoading();
        compositeDisposable.add(mealRepository.getRandomMeal()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            if (view != null) {
                                view.hideLoading();
                                if (meals != null && !meals.isEmpty()) {
                                    view.showMeal(meals.get(0));
                                }
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            }
                        }
                )
        );
    }

    @Override
    public void getCachedMeal(String currentDate) {
        String cachedMealJson = userRepository.getCachedMeal();
        String lastDate = userRepository.getLastMealDate();

        if (cachedMealJson != null && lastDate.equals(currentDate)) {
            Meal cachedMeal = new Gson().fromJson(cachedMealJson, Meal.class);
            if (view != null) {
                view.showMeal(cachedMeal);
            }
        } else {
            getRandomMeal();
        }
    }

    @Override
    public void getCategories() {
        compositeDisposable.add(mealRepository.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categories -> {
                            if (view != null) {
                                int limit = Math.min(categories.size(), 5);
                                List<Category> firstFive = new ArrayList<>(categories.subList(0, limit));
                                view.showCategories(firstFive);
                                preloadCategoryMeals(firstFive);
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.showError(error.getMessage());
                            }
                        }
                )
        );
    }

    private void preloadCategoryMeals(List<Category> categories) {
        compositeDisposable.add(io.reactivex.rxjava3.core.Observable.fromIterable(categories)
                .concatMap(category ->
                        mealRepository.filterBy("Category", category.strCategory).toObservable()
                )
                .subscribeOn(Schedulers.io()).subscribe());
    }

    @Override
    public void getFavoritesCount() {
        compositeDisposable.add(mealRepository.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            if (view != null) {
                                view.showFavoritesCount(meals.size());
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.showError(error.getMessage());
                            }
                        }
                )
        );
    }

    @Override
    public void addToPlan(Meal meal, Date date) {
        if (meal == null || date == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String type;
        if (hour >= 5 && hour < 11) {
            type = "BREAKFAST";
        } else if (hour >= 11 && hour < 16) {
            type = "LUNCH";
        } else {
            type = "DINNER";
        }

        addToPlan(meal, date, type);
    }

    @Override
    public void addToPlan(Meal meal, Date date, String mealType) {
        if (meal == null || date == null || mealType == null) return;

        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        String dateStr = dbDateFormat.format(date);
        String dayOfWeek = dayFormat.format(date);

        compositeDisposable.add(mealRepository.addPlan(meal, dateStr, dayOfWeek, mealType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> { if (view != null) view.onPlanAddedSuccess(); },
                        error -> { if (view != null) view.onPlanAddedError(error.getMessage()); }
                ));
    }

    @Override
    public void getPlansCount() {
        compositeDisposable.add(mealRepository.getPlansCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        count -> {
                            if (view != null) {
                                view.showPlansCount(count);
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.showError(error.getMessage());
                            }
                        }
                )
        );
    }

    @Override
    public void getTodaysPlan() {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayStr = dbDateFormat.format(new Date());

        compositeDisposable.add(mealRepository.getPlansByDate(todayStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        plans -> {
                            if (view != null) {
                                if (!plans.isEmpty()) {
                                    PlannedMealDTO plan = plans.get(0);
                                    view.showTodaysPlan(plan);
                                    cachePlanDetails(plan.mealId);
                                } else {
                                    view.showTodaysPlan(null);
                                }
                            }
                        }
                )
        );
    }

    private void cachePlanDetails(String mealId) {
        compositeDisposable.add(mealRepository.getMealById(mealId)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void logout() {
        userRepository.logout();
        if (view != null) {
            view.navigateToLogin();
        }
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