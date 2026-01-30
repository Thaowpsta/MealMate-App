package com.example.mealmate.ui.sign_up;

import android.app.Activity;
import android.content.Intent;

public interface SignUpContract {
    interface View {
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

    interface Presenter {
        void signup(String name, String email, String password);
        void onGoogleSignInClicked(Activity activity);
        void onGoogleResultReceived(Intent data);
        void onFacebookTokenReceived(String token);
        void onDestroy();
    }
}
