package com.example.mealmate.ui.meal_details.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.meal_details.view.MealDetailsView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MealDetailsPresenterImp implements MealDetailsPresenter {

    private MealDetailsView view;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final Context context;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public MealDetailsPresenterImp(MealDetailsView view, Context context) {
        this.view = view;
        this.context = context;
        this.mealRepository = new MealRepository(context);
        this.userRepository = new UserRepository(context);
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
    public void getMealDetails(Meal meal) {
        if (view == null || meal == null) return;

        if (!isNetworkAvailable()) {
            meal.strYoutube = null;
        }

        view.showMeal(meal);

        if (meal.getStrInstructions() == null || meal.getStrInstructions().isEmpty() || meal.getStrInstructions().startsWith("Planned for")) {
            view.showLoading();
            disposable.add(mealRepository.getMealById(meal.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            fullMeal -> {
                                if (view != null) {
                                    view.hideLoading();
                                    if (!isNetworkAvailable()) {
                                        fullMeal.strYoutube = null;
                                    }

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
            checkFavoriteStatus(meal);
        }
    }

    private void checkFavoriteStatus(Meal meal) {
        if (meal == null || view == null) return;
        disposable.add(mealRepository.isFavorite(meal.getId())
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
                                view.showMeal(meal);
                            }
                        }
                ));
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        if (meal == null) return;

        if (userRepository.isGuest()) {
            if (view != null) view.showGuestLoginDialog();
            return;
        }

        if (!isNetworkAvailable()) {
            if (view != null) view.showConnectionError();
            return;
        }

        if (meal.isFavorite) {
            removeFromFavorites(meal);
        } else {
            addToFavorites(meal);
        }
    }

    @Override
    public void onAddToPlanClicked(Meal meal) {
        if (meal == null) {
            if (view != null) view.showError(context.getString(R.string.no_meal_loaded));
            return;
        }

        if (userRepository.isGuest()) {
            if (view != null) view.showGuestLoginDialog();
            return;
        }

        if (!isNetworkAvailable()) {
            if (view != null) view.showConnectionError();
            return;
        }

        if (view != null) view.showWeekCalendarDialog();
    }

    @Override
    public void addToFavorites(Meal meal) {
        if (meal == null) return;
        disposable.add(mealRepository.addFavorite(meal)
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
    public void addToPlan(Meal meal, Date date, String mealType) {
        if (meal == null || date == null || mealType == null) return;

        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        String dateStr = dbDateFormat.format(date);
        String dayOfWeek = dateFormat.format(date);

        disposable.add(mealRepository.addPlan(meal, dateStr, dayOfWeek, mealType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> { if (view != null) view.onPlanAddedSuccess(); },
                        error -> { if (view != null) view.onPlanAddedError(error.getMessage()); }
                ));
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        if (meal == null) return;
        disposable.add(mealRepository.removeFavorite(meal)
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
    public void logout() {
        userRepository.logout();
        if (view != null) {
            view.navigateToLogin();
        }
    }

    @Override
    public void onDestroy() {
        view = null;
        disposable.clear();
    }
}