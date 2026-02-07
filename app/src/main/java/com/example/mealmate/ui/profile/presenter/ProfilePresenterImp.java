package com.example.mealmate.ui.profile.presenter;

import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.profile.view.ProfileView;

public class ProfilePresenterImp implements ProfilePresenter {
    private final ProfileView view;
    private final UserRepository repository;

    public ProfilePresenterImp(ProfileView view, UserRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getUserProfile() {
        String name = repository.getUserDisplayName();
        String email = repository.getUserEmail();
        view.showUserProfile(name, email);
    }

    @Override
    public void getSettings() {
        // Check Theme
        String theme = repository.getThemeMode();
        boolean isDark = "dark".equalsIgnoreCase(theme);
        view.setDarkThemeSwitch(isDark);

        // Check Language
        String lang = repository.getLanguage();
        boolean isArabic = "ar".equalsIgnoreCase(lang);
        view.setLanguageSwitch(isArabic);
    }

    @Override
    public void updateTheme(boolean isDark) {
        String mode = isDark ? "dark" : "light";
        // Pass the current language to keep it consistent
        repository.saveProfileSettings(mode, repository.getLanguage());
    }

    @Override
    public void updateLanguage(boolean isArabic) {
        String lang = isArabic ? "ar" : "en";
        // Pass the current theme to keep it consistent
        repository.saveProfileSettings(repository.getThemeMode(), lang);
    }
}