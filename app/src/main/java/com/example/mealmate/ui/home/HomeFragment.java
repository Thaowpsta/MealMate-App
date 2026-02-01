package com.example.mealmate.ui.home;

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

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.models.Meal;
import com.google.android.material.chip.Chip;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;

    private TextView mealTitle;
    private ImageView mealImage;
    private Chip areaChip, categoryChip;
    private ImageButton refreshButton;
    private Meal currentMeal;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new HomePresenter(this);
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
        CardView modCard = view.findViewById(R.id.mod_card);

         areaChip = view.findViewById(R.id.meal_country);
         categoryChip = view.findViewById(R.id.meal_category);

        if (currentMeal == null) {
            presenter.getRandomMeal();
        } else {
            showMeal(currentMeal);
        }

        refreshButton.setOnClickListener(v -> presenter.getRandomMeal());

        modCard.setOnClickListener(v -> presenter.onMealClicked(currentMeal));
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
    public void showError(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}