package com.example.mealmate.ui.sign_up;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private UserRepository userRepository;
    private GoogleSignInClient googleSignInClient;

    public SignUpPresenter(SignUpContract.View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
    }

    @Override
    public void signup(String name, String email, String password) {
        if (TextUtils.isEmpty(name) || name.trim().isEmpty()) {
            view.showNameError("Name is required");
            return;
        }

        if (name.trim().length() < 3) {
            view.showNameError("Name must be at least 3 characters");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            view.showEmailError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError("Please enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            view.showPasswordError("Password is required");
            return;
        }

        if (password.length() < 6) {
            view.showPasswordError("Password must be at least 6 characters");
            return;
        }

//        if (TextUtils.isEmpty(confirmPassword)) {
//            view.showConfirmPasswordError("Please confirm your password");
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            view.showConfirmPasswordError("Passwords do not match");
//            return;
//        }

        view.showProgress();

        userRepository.signUp(email, password, new UserRepository.RegisterCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (user != null) {
                    updateUserProfile(user, name.trim());
                } else {
                    view.hideProgress();
                    view.onSignUpSuccess();
                }
            }

            @Override
            public void onError(String error) {
                view.hideProgress();
                view.onSignUpError(error);
            }
        });
    }

    private void updateUserProfile(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    view.hideProgress();
                    view.onSignUpSuccess();
                })
                .addOnFailureListener(e -> {
                    view.hideProgress();
                    view.onSignUpSuccess();
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
                        if (view != null) { view.hideProgress(); view.onSignUpSuccess(); }
                    }
                    @Override
                    public void onError(String error) {
                        if (view != null) { view.hideProgress(); view.onSignUpError(error); }
                    }
                });
            }
        } catch (ApiException e) {
            if (view != null) view.onSignUpError("Google sign in failed: " + e.getMessage());
        }
    }

    @Override
    public void onFacebookTokenReceived(String token) {
        view.showProgress();
        userRepository.firebaseAuthWithFacebook(token, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (view != null) { view.hideProgress(); view.onSignUpSuccess(); }
            }
            @Override
            public void onError(String error) {
                if (view != null) { view.hideProgress(); view.onSignUpError(error); }
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}