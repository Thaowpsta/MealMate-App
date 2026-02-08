package com.example.mealmate.ui.profile.presenter;

public interface ProfilePresenter {
    void getUserProfile();
    void getSettings();
    void checkNetworkStatus();
    void onThemeChanged(boolean isDark);
    void onLanguageChanged(boolean isArabic);
    void onSaveClicked();
}