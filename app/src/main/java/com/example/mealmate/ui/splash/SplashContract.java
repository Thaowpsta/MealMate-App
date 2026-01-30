package com.example.mealmate.ui.splash;

public interface SplashContract {

    interface View {
        void navigateToLogin();
        void navigateToHome();
    }

    interface Presenter {
        void checkLoginStatus();
        void onDestroy();
    }
}