package com.example.mealmate.ui.splash.presenter;

import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.splash.view.SplashView;

public class SplashPresenterImp implements SplashPresenter {

    private SplashView view;
    private UserRepository userRepository;

    public SplashPresenterImp(SplashView view, UserRepository userRepository) {
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
