package com.example.mealmate.ui.auth.sign_up.presenter;

import android.app.Activity;
import android.content.Intent;

public interface SignUpPresenter {
    void signup(String name, String email, String password);

    void loginGuest();

    void onGoogleSignInClicked(Activity activity);

    void onGoogleResultReceived(Intent data);

    void onFacebookTokenReceived(String token);

    void onDestroy();
}
