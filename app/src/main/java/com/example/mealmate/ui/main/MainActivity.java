package com.example.mealmate.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.mealmate.R;
import com.example.mealmate.data.SharedPreferencesManager;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.splash.view.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferencesManager prefs = SharedPreferencesManager.getInstance(this);

        String theme = prefs.getThemeMode();
        if ("dark".equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        String lang = prefs.getLanguage();
        setLocale(lang);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Standard setup
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Guest Restriction Logic
        UserRepository userRepository = new UserRepository(this);
        bottomNav.setOnItemSelectedListener(item -> {
            if (userRepository.isGuest()) {
                int id = item.getItemId();
                // Check if the destination is one of the restricted fragments
                if (id == R.id.searchFragment || id == R.id.favoritesFragment || id == R.id.profileFragment || id == R.id.plannerFragment) {
                    showGuestLoginDialog();
                    return false; // Cancel navigation
                }
            }
            // Proceed with normal navigation
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) ->{
            if(destination.getId() == R.id.profileFragment)
                bottomNav.setVisibility(View.GONE);
            else
                bottomNav.setVisibility(View.VISIBLE);
        });
    }

    private void showGuestLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Guest Mode")
                .setMessage("You need to login to use this feature.")
                .setPositiveButton(R.string.login, (dialog, which) -> {
                    Intent intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("IS_LOGOUT", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}