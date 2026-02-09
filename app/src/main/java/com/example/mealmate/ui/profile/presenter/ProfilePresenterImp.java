package com.example.mealmate.ui.profile.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.profile.view.ProfileView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenterImp implements ProfilePresenter {
    private final ProfileView view;
    private final UserRepository repository;
    private final MealRepository mealRepository;
    private final Context context;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ProfilePresenterImp(ProfileView view, Context context) {
        this.view = view;
        this.repository = new UserRepository(context);
        this.mealRepository = new MealRepository(context);
        this.context = context;
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
    public void checkNetworkStatus() {
        if (view != null) {
            view.setSaveButtonVisible(isNetworkAvailable());
        }
    }

    @Override
    public void getUserProfile() {
        if (view != null) {
            String name = repository.getUserDisplayName();
            String email = repository.getUserEmail();
            view.showUserProfile(name, email);
        }
    }

    @Override
    public void getSettings() {
        if (view != null) {
            String theme = repository.getThemeMode();
            boolean isDark = "dark".equalsIgnoreCase(theme);
            view.setDarkThemeSwitch(isDark);

            String lang = repository.getLanguage();
            boolean isArabic = "ar".equalsIgnoreCase(lang);
            view.setLanguageSwitch(isArabic);
        }
    }

    @Override
    public void onThemeChanged(boolean isDark) {
        String mode = isDark ? "dark" : "light";
        repository.saveProfileSettings(mode, repository.getLanguage());
        if (view != null) {
            view.applyTheme(isDark);
        }
    }

    @Override
    public void onLanguageChanged(boolean isArabic) {
        String lang = isArabic ? "ar" : "en";
        repository.saveProfileSettings(repository.getThemeMode(), lang);
        if (view != null) {
            view.restartActivity();
        }
    }

    @Override
    public void onSaveClicked(String name, String password) {
        if (!isNetworkAvailable()) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.updateProfile(name, password, new UserRepository.UpdateProfileCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                if (view != null) view.navigateToHome();
            }

            @Override
            public void onError(String error) {
                if (error.toLowerCase().contains("sensitive") || error.toLowerCase().contains("recent")) {
                    Toast.makeText(context, "Security: Please Log Out and Log In again to change password.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to update profile: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                        }
                )
        );
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
                        }
                )
        );
    }

}