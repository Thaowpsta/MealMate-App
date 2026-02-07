package com.example.mealmate.ui.profile.view;

public interface ProfileView {
    void showUserProfile(String name, String email);
    void setDarkThemeSwitch(boolean isDark);
    void setLanguageSwitch(boolean isArabic);
}