package com.example.mealmate.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.models.Meal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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
    private static final String KEY_CACHED_CATEGORIES = "cached_categories";
    private static final String KEY_CATEGORIES_TIMESTAMP = "categories_timestamp";
    private static final long CACHE_DURATION_24H = 24 * 60 * 60 * 1000;

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

    public void cacheMealList(String key, List<Meal> meals) {
        if (meals == null || meals.isEmpty()) return;
        String json = new Gson().toJson(meals);
        editor.putString(key, json);
        editor.apply();
    }

    public List<Meal> getCachedMealList(String key) {
        String json = sharedPreferences.getString(key, null);
        if (json == null) return null;

        Type type = new TypeToken<List<Meal>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    // ==================== INDIVIDUAL MEAL CACHING (NEW) ====================
    public void cacheMealDetails(Meal meal) {
        if (meal == null || meal.getId() == null) return;
        String json = new Gson().toJson(meal);
        editor.putString("meal_details_" + meal.getId(), json);
        editor.apply();
    }

    public Meal getCachedMealDetails(String id) {
        String json = sharedPreferences.getString("meal_details_" + id, null);
        if (json == null) return null;
        return new Gson().fromJson(json, Meal.class);
    }

// ==================== Categories Caching ====================
    public void cacheCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) return;
        String json = new Gson().toJson(categories);
        editor.putString(KEY_CACHED_CATEGORIES, json);
        editor.putLong(KEY_CATEGORIES_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    public List<Category> getCachedCategories() {
        long lastCacheTime = sharedPreferences.getLong(KEY_CATEGORIES_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCacheTime > CACHE_DURATION_24H) {
            return null;
        }

        String json = sharedPreferences.getString(KEY_CACHED_CATEGORIES, null);
        if (json == null) return null;

        Type type = new TypeToken<List<Category>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public void cacheCategoryMeals(String categoryName, List<Meal> meals) {
        if (meals == null || meals.isEmpty() || categoryName == null) return;
        String json = new Gson().toJson(meals);
        String key = "category_" + categoryName.toLowerCase().replace(" ", "_") + "_meals";
        editor.putString(key, json);
        editor.apply();
    }

    public List<Meal> getCachedCategoryMeals(String categoryName) {
        if (categoryName == null) return null;
        String key = "category_" + categoryName.toLowerCase().replace(" ", "_") + "_meals";
        String json = sharedPreferences.getString(key, null);
        if (json == null) return null;

        Type type = new TypeToken<List<Meal>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public void clearCategoryCache() {
        editor.remove(KEY_CACHED_CATEGORIES);
        editor.remove(KEY_CATEGORIES_TIMESTAMP);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (String key : allEntries.keySet()) {
            if (key.startsWith("category_") && key.endsWith("_meals")) {
                editor.remove(key);
            }
        }
        editor.apply();
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