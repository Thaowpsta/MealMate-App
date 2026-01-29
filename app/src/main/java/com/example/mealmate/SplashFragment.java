package com.example.mealmate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashFragment extends Fragment {

    LottieAnimationView animationView;
    TextView splashSubtitle;
    TextView splashTitle;


    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splashSubtitle = view.findViewById(R.id.splash_subtitle);
        splashTitle = view.findViewById(R.id.splash_title);
        animationView = view.findViewById(R.id.lottieAnimationView);

        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        splashSubtitle.startAnimation(slideUp);
        splashTitle.startAnimation(slideUp);

        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginFragment);

//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
            }
        });
    }
}