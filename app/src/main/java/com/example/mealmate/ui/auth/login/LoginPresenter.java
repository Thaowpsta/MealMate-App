package com.example.mealmate.ui.auth.login;

import android.app.Activity;
import android.content.Intent;

import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private UserRepository userRepository;
    private GoogleSignInClient googleSignInClient;

    public LoginPresenter(LoginContract.View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
    }

    @Override
    public void login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            view.showEmailError("Email is required");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            view.showPasswordError("Password is required");
            return;
        }

        if (password.length() < 6) {
            view.showPasswordError("Password must be at least 6 characters");
            return;
        }

        view.showProgress();

        userRepository.login(email, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (view != null) {
                    view.hideProgress();
                    view.onLoginSuccess();
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.hideProgress();
                    view.onLoginError(error);
                }
            }
        });
    }

    @Override
    public void loginGuest() {
        view.showProgress();
        userRepository.loginGuest(new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (view != null) {
                    view.hideProgress();
                    view.onLoginSuccess();
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.hideProgress();
                    view.onLoginError(error);
                }
            }
        });
    }

    @Override
    public void onGoogleSignInClicked(Activity activity) {
        if (googleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(activity, gso);
        }

        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            view.launchGoogleSignIn(signInIntent);
        });
    }

    @Override
    public void onGoogleResultReceived(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                view.showProgress();
                userRepository.firebaseAuthWithGoogle(account.getIdToken(), new UserRepository.LoginCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        if (view != null) { view.hideProgress(); view.onLoginSuccess(); }
                    }
                    @Override
                    public void onError(String error) {
                        if (view != null) { view.hideProgress(); view.onLoginError(error); }
                    }
                });
            }
        } catch (ApiException e) {
            if (view != null) view.onLoginError("Google sign in failed: " + e.getMessage());
        }
    }

    @Override
    public void onFacebookTokenReceived(String token) {
        view.showProgress();
        userRepository.firebaseAuthWithFacebook(token, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (view != null) { view.hideProgress(); view.onLoginSuccess(); }
            }
            @Override
            public void onError(String error) {
                if (view != null) { view.hideProgress(); view.onLoginError(error); }
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}