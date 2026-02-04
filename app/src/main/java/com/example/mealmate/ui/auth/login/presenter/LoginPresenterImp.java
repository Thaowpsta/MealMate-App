package com.example.mealmate.ui.auth.login.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.auth.login.view.LoginView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenterImp implements LoginPresenter{

    private LoginView loginView;
    private final UserRepository repository;
    private GoogleSignInClient googleSignInClient;

    public LoginPresenterImp(LoginView loginView, Context context) {
        this.loginView = loginView;
        repository = new UserRepository(context);

    }

    @Override
    public void login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            loginView.showEmailError("Email is required");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            loginView.showPasswordError("Password is required");
            return;
        }

        if (password.length() < 6) {
            loginView.showPasswordError("Password must be at least 6 characters");
            return;
        }

        loginView.showProgress();

        repository.login(email, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (loginView != null) {
                    loginView.hideProgress();
                    loginView.onLoginSuccess();
                }
            }

            @Override
            public void onError(String error) {
                if (loginView != null) {
                    loginView.hideProgress();
                    loginView.onLoginError(error);
                }
            }
        });
    }

    @Override
    public void loginGuest() {
        loginView.showProgress();
        repository.loginGuest(new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (loginView != null) {
                    loginView.hideProgress();
                    loginView.onLoginSuccess();
                }
            }

            @Override
            public void onError(String error) {
                if (loginView != null) {
                    loginView.hideProgress();
                    loginView.onLoginError(error);
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
            loginView.launchGoogleSignIn(signInIntent);
        });
    }

    @Override
    public void onGoogleResultReceived(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                loginView.showProgress();
                repository.firebaseAuthWithGoogle(account.getIdToken(), new UserRepository.LoginCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        if (loginView != null) { loginView.hideProgress(); loginView.onLoginSuccess(); }
                    }
                    @Override
                    public void onError(String error) {
                        if (loginView != null) { loginView.hideProgress(); loginView.onLoginError(error); }
                    }
                });
            }
        } catch (ApiException e) {
            if (loginView != null) loginView.onLoginError("Google sign in failed: " + e.getMessage());
        }
    }

    @Override
    public void onFacebookTokenReceived(String token) {
        loginView.showProgress();
        repository.firebaseAuthWithFacebook(token, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (loginView != null) { loginView.hideProgress(); loginView.onLoginSuccess(); }
            }
            @Override
            public void onError(String error) {
                if (loginView != null) { loginView.hideProgress(); loginView.onLoginError(error); }
            }
        });
    }

    @Override
    public void onDestroy() {
       loginView = null;
    }
}
