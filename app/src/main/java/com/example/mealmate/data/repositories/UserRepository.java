package com.example.mealmate.data.repositories;

import android.content.Context;

import com.example.mealmate.data.SharedPreferencesManager;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class UserRepository {

    private final FirebaseAuth firebaseAuth;
    private final SharedPreferencesManager sharedPreferencesManager;

    public UserRepository(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
    }

    public void loginGuest(LoginCallback callback) {
        firebaseAuth.signInAnonymously().addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if(user != null) {
                sharedPreferencesManager.saveUserProfile(
                        user.getUid(),
                        "Guest",
                        "",
                        ""
                );
            }
            if(callback != null)
                callback.onSuccess(user);
        }).addOnFailureListener(e -> {
            if(callback != null)
                callback.onError(e.getMessage());
        });
    }

    public void login(String email, String password, LoginCallback callback ){

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();

            if(callback != null)
                callback.onSuccess(authResult.getUser());

            if(user != null)
                saveUserToPrefs(user);

        }).addOnFailureListener(error -> {
            if (callback != null)
                callback.onError(error.getMessage());
        });
    }

    public void sendLoginLinkToEmail(String email, SendLoginLinkToEmailCallback callback){
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder().setUrl("https://mealmate.page.link/login").setHandleCodeInApp(true).setAndroidPackageName("com.example.mealmate", true, null).build();

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).addOnSuccessListener(res -> {
            sharedPreferencesManager.savePendingEmail(email);
            if (callback != null)
                callback.onSuccess();
        }).addOnFailureListener(error -> {
            if (callback != null)
                callback.onError(error.getMessage());
        });
    }

    public void loginWithEmailLink(String email, String emailLink, loginWithEmailLinkCallback callback){
        if (firebaseAuth.isSignInWithEmailLink(emailLink)){
            firebaseAuth.signInWithEmailLink(email, emailLink).addOnSuccessListener(authResult -> {

                FirebaseUser user = authResult.getUser();
                if (user != null)
                    saveUserToPrefs(user);
                if (callback != null)
                    callback.onSuccess(authResult.getUser());

            }).addOnFailureListener(error -> {
                if (callback != null)
                    callback.onError(error.getMessage());
            });
        }else {
            if (callback != null) {
                callback.onError("Invalid email link");
            }
        }
    }

    public boolean isSignInWithEmailLink(String emailLink) {
        return firebaseAuth.isSignInWithEmailLink(emailLink);
    }


    public void signUp(String email, String password, RegisterCallback callback){

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if (user != null)
                saveUserToPrefs(user);
            if (callback != null)
                callback.onSuccess(authResult.getUser());
        }).addOnFailureListener(error -> {
            if (callback != null)
                callback.onError(error.getMessage());
        });
    }

    public void firebaseAuthWithGoogle(String idToken, LoginCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        signInWithCredential(credential, callback);
    }

    public void firebaseAuthWithFacebook(String token, LoginCallback callback) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        signInWithCredential(credential, callback);
    }

    private void signInWithCredential(AuthCredential credential, LoginCallback callback) {
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        saveUserToPrefs(user);
                    }
                    if (callback != null) {
                        callback.onSuccess(user);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferencesManager.isLoggedIn() && getCurrentUser() != null;
    }

    public void logout() {
        firebaseAuth.signOut();
        sharedPreferencesManager.clearUserData();

    }

    public void saveUserToPrefs(FirebaseUser user) {
        if (user != null) {
            sharedPreferencesManager.saveUserProfile(
                    user.getUid(),
                    user.getDisplayName() != null ? user.getDisplayName() : "",
                    user.getEmail() != null ? user.getEmail() : "",
                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : ""
            );
        }
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }

    public interface RegisterCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }

    public interface SendLoginLinkToEmailCallback {
        void onSuccess();
        void onError(String error);
    }
    public interface loginWithEmailLinkCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }
}
