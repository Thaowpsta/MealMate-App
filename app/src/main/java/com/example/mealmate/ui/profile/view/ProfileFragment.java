package com.example.mealmate.ui.profile.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.main.MainActivity;
import com.example.mealmate.ui.profile.presenter.ProfilePresenter;
import com.example.mealmate.ui.profile.presenter.ProfilePresenterImp;

public class ProfileFragment extends Fragment implements ProfileView {

    private ProfilePresenter presenter;
    private TextView usernameTv, emailTv, favNum, plansNum;
    private EditText nameEt, passwordEt;
    private Switch darkModeSwitch, arabicSwitch;
    private AppCompatButton saveButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameTv = view.findViewById(R.id.username);
        emailTv = view.findViewById(R.id.email);
        nameEt = view.findViewById(R.id.change_name);
        passwordEt = view.findViewById(R.id.change_password);
        passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        favNum = view.findViewById(R.id.fav_num);
        plansNum = view.findViewById(R.id.planed_num);
        darkModeSwitch = view.findViewById(R.id.dark_mode);
        arabicSwitch = view.findViewById(R.id.arabic);
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        saveButton = view.findViewById(R.id.save);
        CardView favoritesCard = view.findViewById(R.id.fav_card);
        CardView plansCard = view.findViewById(R.id.planner_card);

        presenter = new ProfilePresenterImp(this, requireContext());

        presenter.getUserProfile();
        presenter.getSettings();
        presenter.checkNetworkStatus();
        presenter.getFavoritesCount();
        presenter.getPlansCount();

        darkModeSwitch.setOnClickListener(v -> {
            presenter.onThemeChanged(darkModeSwitch.isChecked());
        });

        arabicSwitch.setOnClickListener(v -> {
            presenter.onLanguageChanged(arabicSwitch.isChecked());
        });

        saveButton.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            presenter.onSaveClicked(name, password);
        });

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        favoritesCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_favoritesFragment);
        });

        plansCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_plannerFragment);
        });

    }

    @Override
    public void showUserProfile(String name, String email) {
        usernameTv.setText(name);
        emailTv.setText(email);
        nameEt.setText(name);
    }

    @Override
    public void setDarkThemeSwitch(boolean isDark) {
        darkModeSwitch.setChecked(isDark);
    }

    @Override
    public void setLanguageSwitch(boolean isArabic) {
        arabicSwitch.setChecked(isArabic);
    }

    @Override
    public void setSaveButtonVisible(boolean isVisible) {
        if (saveButton != null) {
            saveButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void applyTheme(boolean isDark) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void restartActivity() {
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void showFavoritesCount(int count) {
        favNum.setText(String.valueOf(count));
    }

    @Override
    public void showPlansCount(int count) {
        if(plansNum != null) {
            plansNum.setText(String.valueOf(count));
        }
    }
}