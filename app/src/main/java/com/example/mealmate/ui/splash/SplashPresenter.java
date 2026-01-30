package com.example.mealmate.ui.splash;

import com.example.mealmate.data.repositories.UserRepository;

public class SplashPresenter implements SplashContract.Presenter{

    private SplashContract.View view;
    private UserRepository userRepository;

    public SplashPresenter(SplashContract.View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
    }


    @Override
    public void checkLoginStatus() {
        if (userRepository.isUserLoggedIn())
            view.navigateToHome();
        else
            view.navigateToLogin();
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
