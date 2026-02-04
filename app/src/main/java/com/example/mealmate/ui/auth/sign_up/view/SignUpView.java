package com.example.mealmate.ui.auth.sign_up.view;

import android.content.Intent;

public interface SignUpView {
        void showProgress();

        void hideProgress();

        void onSignUpSuccess();

        void onSignUpError(String message);

        void showNameError(String error);

        void showEmailError(String error);

        void showPasswordError(String error);

        void showConfirmPasswordError(String error);
        void launchGoogleSignIn(Intent intent);
    }
