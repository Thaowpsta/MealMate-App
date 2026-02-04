package com.example.mealmate.ui.home.view;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.categories.view.CategoriesAdapter;
import com.example.mealmate.ui.home.presenter.HomePresenter;
import com.example.mealmate.ui.home.presenter.HomePresenterImp;
import com.example.mealmate.ui.splash.view.SplashActivity;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements HomeView {

    private HomePresenter presenter;

    private TextView mealTitle;
    private ImageView mealImage;
    private Chip areaChip, categoryChip;
    private ImageButton refreshButton;
    private UserRepository userRepository;
    private Meal currentMeal;
    private RecyclerView rvCategories;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(requireContext());
        presenter = new HomePresenterImp(this, new MealRepository(), userRepository);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealTitle = view.findViewById(R.id.meal_title);
        mealImage = view.findViewById(R.id.meal_bg_img);
        refreshButton = view.findViewById(R.id.mod_refresh);
        ImageButton logoutButton = view.findViewById(R.id.logout);
        CardView modCard = view.findViewById(R.id.mod_card);
        areaChip = view.findViewById(R.id.meal_country);
        categoryChip = view.findViewById(R.id.meal_category);
        TextView usernameTxt = view.findViewById(R.id.username);
        ImageView userImg = view.findViewById(R.id.user_img);
        TextView date = view.findViewById(R.id.date);
        rvCategories = view.findViewById(R.id.rv_categories);
        TextView seeAll = view.findViewById(R.id.see_all);


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM. d", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        date.setText(currentDate);

        FirebaseUser currentUser = userRepository.getCurrentUser();
        if (currentUser != null) {
            userRepository.saveUserToPrefs(currentUser);

            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (name == null || name.isEmpty()) {
                if (email != null && email.contains("@")) {
                    name = email.split("@")[0];
                } else {
                    name = "Guest";
                }
            }
            usernameTxt.setText(name);

            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.user)
                        .circleCrop()
                        .into(userImg);
            }
        }

        if (currentMeal == null) {
            presenter.getRandomMeal();
        } else {
            showMeal(currentMeal);
        }

        presenter.getCategories();

        refreshButton.setOnClickListener(v -> presenter.getRandomMeal());

        logoutButton.setOnClickListener(v -> {
            presenter.logout();
        });

        modCard.setOnClickListener(v -> {
            if (currentMeal != null) {
                presenter.onMealClicked(currentMeal);
            }
        });

        seeAll.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_categoriesFragment);
            }
        });
    }

    @Override
    public void showLoading() {
        refreshButton.setEnabled(false);
        mealTitle.setText(R.string.loading);
    }

    @Override
    public void hideLoading() {
        refreshButton.setEnabled(true);
    }

    @Override
    public void showMeal(Meal meal) {
        currentMeal = meal;
        mealTitle.setText(meal.strMeal);
        categoryChip.setText(meal.strCategory);
        areaChip.setText(meal.strArea);

        Glide.with(this)
                .load(meal.strMealThumb)
                .error(R.drawable.medium)
                .into(mealImage);
    }

    @Override
    public void navigateToMealDetails(Meal meal) {
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToMealDetailsFragment(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void showCategories(List<Category> categories) {
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories, CategoriesAdapter.VIEW_TYPE_CHIP);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoriesAdapter);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(requireContext(), SplashActivity.class);
        intent.putExtra("IS_LOGOUT", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}