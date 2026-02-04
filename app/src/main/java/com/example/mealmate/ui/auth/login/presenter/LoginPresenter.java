package com.example.mealmate.ui.auth.login.presenter;

import android.app.Activity;
import android.content.Intent;

public interface LoginPresenter {
        void login(String email, String password);
        void loginGuest();
        void onGoogleSignInClicked(Activity activity);
        void onGoogleResultReceived(Intent data);
        void onFacebookTokenReceived(String token);
        void onDestroy();
    }