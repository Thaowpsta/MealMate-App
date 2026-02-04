package com.example.mealmate.ui.meal_details.view;

import android.os.Bundle;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.meals.model.Meal;
import com.example.mealmate.ui.meal_details.presenter.MealDetailsPresenter;
import com.example.mealmate.ui.meal_details.presenter.MealDetailsPresenterImp;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private MealDetailsPresenter presenter;
    private ImageView mealImage;
    private TextView mealTitle;
    private Chip areaChip, categoryChip, itemNumber;
    private int counter = 0;
    private RecyclerView rvIngredients;
    private RecyclerView rvInstructions;
    private TextView seeMoreSteps;
    private InstructionsAdapter instructionsAdapter;
    private WebView videoWebView;

    public MealDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MealDetailsPresenterImp(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealImage = view.findViewById(R.id.meal_bg_img);
        areaChip = view.findViewById(R.id.meal_country);
        categoryChip = view.findViewById(R.id.meal_category);
        mealTitle = view.findViewById(R.id.meal_title);
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        ImageButton btnFavorite = view.findViewById(R.id.btn_favorite);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        itemNumber = view.findViewById(R.id.item_num);
        rvInstructions = view.findViewById(R.id.rv_instructions);
        seeMoreSteps = view.findViewById(R.id.see_more_steps);
        videoWebView = view.findViewById(R.id.vv_video);

        if (getArguments() != null) {
            MealDetailsFragmentArgs args = MealDetailsFragmentArgs.fromBundle(getArguments());
            Meal currentMeal = args.getMeal();

            presenter.getMealDetails(currentMeal);
        }

        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        btnFavorite.setOnClickListener(v -> Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void showMeal(Meal meal) {

        mealTitle.setText(meal.strMeal);
        categoryChip.setText(meal.strCategory);
        areaChip.setText(meal.strArea);

        Glide.with(this)
                .load(meal.strMealThumb)
                .placeholder(R.drawable.medium)
                .error(R.drawable.medium)
                .into(mealImage);

        List<Pair<String, String>> ingredientList = meal.getIngredientsAndMeasures();
        counter = ingredientList.size();

        itemNumber.setText(counter + " item");
        IngredientsAdapter adapter = new IngredientsAdapter(ingredientList);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setAdapter(adapter);

        if (meal.strInstructions != null && !meal.strInstructions.isEmpty()) {
            // The regex "\\r?\\n" handles both \r\n and \n
            String[] stepsArray = meal.strInstructions.split("\\r?\\n");

            List<String> stepsList = new ArrayList<>();
            for (String step : stepsArray) {
                if (!step.trim().isEmpty()) {
                    stepsList.add(step.trim());
                }
            }

            instructionsAdapter = new InstructionsAdapter(stepsList);
            rvInstructions.setLayoutManager(new LinearLayoutManager(getContext()));
            rvInstructions.setAdapter(instructionsAdapter);

            seeMoreSteps.setOnClickListener(v -> {
                boolean isCurrentlyExpanded = instructionsAdapter.isExpanded();
                instructionsAdapter.setExpanded(!isCurrentlyExpanded);

                if (!isCurrentlyExpanded) {
                    seeMoreSteps.setText(R.string.show_less);
                } else {
                    seeMoreSteps.setText(R.string.show_more);
                    rvInstructions.scrollToPosition(0);
                }
            });

            if (stepsList.size() <= 2) {
                seeMoreSteps.setVisibility(View.GONE);
                instructionsAdapter.setExpanded(true);
            } else {
                seeMoreSteps.setVisibility(View.VISIBLE);
            }
        }

        if (meal.strYoutube != null && !meal.strYoutube.isEmpty()) {
            String videoId = "";

            if (meal.strYoutube.contains("v=")) {
                String[] parts = meal.strYoutube.split("v=");
                if (parts.length > 1) {
                    videoId = parts[1];
                }
            }

            if (!videoId.isEmpty()) {
                String embedUrl = "https://www.youtube.com/embed/" + videoId;
                videoWebView.loadUrl(embedUrl);
            }
        } else {
            videoWebView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}