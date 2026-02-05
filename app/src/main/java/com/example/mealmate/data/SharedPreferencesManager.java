package com.example.mealmate.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "MealMatePrefs";
    private static SharedPreferencesManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String KEY_PENDING_EMAIL = "pending_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_PROFILE_IMAGE_URL = "profile_image_url";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_LANGUAGE = "language_code";
    private static final String KEY_LAST_MEAL_DATE = "last_meal_date";
    private static final String KEY_CACHED_MEAL = "cached_meal";

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    // ==================== Pending Email (for passwordless login) ====================
    public void savePendingEmail(String email) {
        editor.putString(KEY_PENDING_EMAIL, email);
        editor.apply();
    }

    public String getPendingEmail() {
        return sharedPreferences.getString(KEY_PENDING_EMAIL, null);
    }

    public void clearPendingEmail() {
        editor.remove(KEY_PENDING_EMAIL);
        editor.apply();
    }

    // ==================== User Data ====================
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void saveUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public void saveUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public void saveProfileImageUrl(String url) {
        editor.putString(KEY_PROFILE_IMAGE_URL, url);
        editor.apply();
    }

    public String getProfileImageUrl() {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URL, "");
    }

    // ==================== Login State ====================
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ==================== Onboarding ====================
    public void setOnboardingCompleted(boolean completed) {
        editor.putBoolean(KEY_ONBOARDING_COMPLETED, completed);
        editor.apply();
    }

    public boolean isOnboardingCompleted() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    // ==================== Settings ====================
    public void setThemeMode(String mode) {
        editor.putString(KEY_THEME_MODE, mode);
        editor.apply();
    }

    public String getThemeMode() {
        return sharedPreferences.getString(KEY_THEME_MODE, "auto");
    }

    public void setLanguage(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "en");
    }

    // ==================== Random Meal Caching ====================
    public void saveCachedMeal(String mealJson, String date) {
        editor.putString(KEY_CACHED_MEAL, mealJson);
        editor.putString(KEY_LAST_MEAL_DATE, date);
        editor.apply();
    }

    public String getCachedMeal() {
        return sharedPreferences.getString(KEY_CACHED_MEAL, null);
    }

    public String getLastMealDate() {
        return sharedPreferences.getString(KEY_LAST_MEAL_DATE, "");
    }

    // ==================== Save Complete User Profile ====================
    public void saveUserProfile(String userId, String name, String email, String profileImageUrl) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_PROFILE_IMAGE_URL, profileImageUrl);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // ==================== Clear All Data (Logout) ====================
    public void clearUserData() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_PROFILE_IMAGE_URL);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_PENDING_EMAIL);

        editor.remove(KEY_CACHED_MEAL);
        editor.remove(KEY_LAST_MEAL_DATE);
        editor.apply();
    }
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}