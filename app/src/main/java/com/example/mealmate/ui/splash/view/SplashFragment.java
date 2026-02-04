package com.example.mealmate.ui.splash.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.main.MainActivity;
import com.example.mealmate.ui.splash.presenter.SplashPresenterImp;

public class SplashFragment extends Fragment implements SplashView {

    LottieAnimationView animationView;
    TextView splashSubtitle;
    TextView splashTitle;
    private SplashPresenterImp presenter;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SplashPresenterImp(this, new UserRepository(requireContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splashSubtitle = view.findViewById(R.id.splash_subtitle);
        splashTitle = view.findViewById(R.id.splash_title);
        animationView = view.findViewById(R.id.lottieAnimationView);

        boolean isLogout = false;
        if (getActivity() != null && getActivity().getIntent() != null) {
            isLogout = getActivity().getIntent().getBooleanExtra("IS_LOGOUT", false);
        }

        if (isLogout) {
            animationView.setVisibility(View.GONE);
            splashTitle.setVisibility(View.GONE);
            splashSubtitle.setVisibility(View.GONE);

            if (presenter != null) {
                presenter.checkLoginStatus();
            }
        } else {
            Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            splashSubtitle.startAnimation(slideUp);
            splashTitle.startAnimation(slideUp);

            animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (presenter != null) {
                        presenter.checkLoginStatus();
                    }
                }
            });
        }
    }

    @Override
    public void navigateToLogin() {
        if (isAdded()) {
            try {
                NavHostFragment.findNavController(this).navigate(R.id.action_splashFragment_to_loginFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void navigateToHome() {
        if (isAdded()) {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}