package com.example.mealmate.ui.auth.login.view;

import android.content.Intent;

public interface LoginView {

    void showProgress();
    void hideProgress();
    void onLoginSuccess();
    void onLoginError(String error);
    void showEmailError(String error);
    void showPasswordError(String error);
    void launchGoogleSignIn(Intent intent);

}
