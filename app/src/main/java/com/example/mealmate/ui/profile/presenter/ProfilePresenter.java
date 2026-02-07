package com.example.mealmate.ui.profile.presenter;

public interface ProfilePresenter {
    void getUserProfile();
    void getSettings();
    void updateTheme(boolean isDark);
    void updateLanguage(boolean isArabic);
}