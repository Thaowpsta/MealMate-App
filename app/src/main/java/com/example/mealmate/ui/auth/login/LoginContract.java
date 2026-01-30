package com.example.mealmate.ui.auth.login;

import android.app.Activity;
import android.content.Intent;

public interface LoginContract {

    interface View{
        void showProgress();
        void hideProgress();
        void onLoginSuccess();
        void onLoginError(String error);
        void showEmailError(String error);
        void showPasswordError(String error);
        void launchGoogleSignIn(Intent intent);
    }

    interface Presenter{
        void login(String email, String password);
        void loginGuest();
        void onGoogleSignInClicked(Activity activity);
        void onGoogleResultReceived(Intent data);
        void onFacebookTokenReceived(String token);
        void onDestroy();
    }
}