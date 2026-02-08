package com.example.mealmate.ui.profile.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.profile.view.ProfileView;

public class ProfilePresenterImp implements ProfilePresenter {
    private final ProfileView view;
    private final UserRepository repository;
    private final Context context;

    public ProfilePresenterImp(ProfileView view, UserRepository repository, Context context) {
        this.view = view;
        this.repository = repository;
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
    public void onSaveClicked() {
        if (view != null) {
            view.navigateToHome();
        }
    }
}