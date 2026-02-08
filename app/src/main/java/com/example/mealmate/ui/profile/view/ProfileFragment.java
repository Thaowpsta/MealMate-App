package com.example.mealmate.ui.profile.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.main.MainActivity;
import com.example.mealmate.ui.profile.presenter.ProfilePresenter;
import com.example.mealmate.ui.profile.presenter.ProfilePresenterImp;

public class ProfileFragment extends Fragment implements ProfileView {

    private ProfilePresenter presenter;
    private TextView usernameTv, emailTv;
    private EditText nameEt, emailEt;
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
        emailEt = view.findViewById(R.id.change_email);
        darkModeSwitch = view.findViewById(R.id.dark_mode);
        arabicSwitch = view.findViewById(R.id.arabic);
        saveButton = view.findViewById(R.id.login);
        saveButton.setText(R.string.save);

        UserRepository repository = new UserRepository(requireContext());
        presenter = new ProfilePresenterImp(this, repository, requireContext());

        presenter.getUserProfile();
        presenter.getSettings();
        presenter.checkNetworkStatus();

        darkModeSwitch.setOnClickListener(v -> {
            presenter.onThemeChanged(darkModeSwitch.isChecked());
        });

        arabicSwitch.setOnClickListener(v -> {
            presenter.onLanguageChanged(arabicSwitch.isChecked());
        });

        saveButton.setOnClickListener(v -> presenter.onSaveClicked());
    }

    @Override
    public void showUserProfile(String name, String email) {
        usernameTv.setText(name);
        emailTv.setText(email);
        nameEt.setText(name);
        emailEt.setText(email);
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
}