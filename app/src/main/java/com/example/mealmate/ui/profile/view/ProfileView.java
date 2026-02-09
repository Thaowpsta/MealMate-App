package com.example.mealmate.ui.profile.view;

public interface ProfileView {
    void showUserProfile(String name, String email);
    void setDarkThemeSwitch(boolean isDark);
    void setLanguageSwitch(boolean isArabic);
    void setSaveButtonVisible(boolean isVisible);
    void applyTheme(boolean isDark);
    void restartActivity();
    void navigateToHome();
    void showPlansCount(int count);
    void showFavoritesCount(int count);
}