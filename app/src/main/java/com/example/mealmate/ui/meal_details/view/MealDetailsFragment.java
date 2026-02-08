package com.example.mealmate.ui.meal_details.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.meal_details.presenter.MealDetailsPresenter;
import com.example.mealmate.ui.meal_details.presenter.MealDetailsPresenterImp;
import com.example.mealmate.ui.plans.view.WeekCalendarAdapter;
import com.example.mealmate.ui.splash.view.SplashActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private MealDetailsPresenter presenter;
    private ImageView mealImage;
    private TextView mealTitle, videoLabel, seeMoreSteps;
    private Chip areaChip, categoryChip, itemNumber;
    private RecyclerView rvIngredients, rvInstructions;
    private InstructionsAdapter instructionsAdapter;
    private YouTubePlayerView youTubePlayerView;
    private ImageButton btnFavorite;
    private Meal currentMeal;
    private Date selectedDateForPlan;
    private CardView videoCard;

    public MealDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MealDetailsPresenterImp(this, requireContext());
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
        btnFavorite = view.findViewById(R.id.btn_favorite);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        itemNumber = view.findViewById(R.id.item_num);
        rvInstructions = view.findViewById(R.id.rv_instructions);
        seeMoreSteps = view.findViewById(R.id.see_more_steps);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        AppCompatButton btnAddToPlan = view.findViewById(R.id.btn_add_to_plan);
        videoCard = view.findViewById(R.id.video_card);
        videoLabel = view.findViewById(R.id.tv_video_label);

        getLifecycle().addObserver(youTubePlayerView);

        if (getArguments() != null) {
            MealDetailsFragmentArgs args = MealDetailsFragmentArgs.fromBundle(getArguments());
            currentMeal = args.getMeal();

            presenter.getMealDetails(currentMeal);
        }

        btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        btnFavorite.setOnClickListener(v -> presenter.onFavoriteClicked(currentMeal));

        btnAddToPlan.setOnClickListener(v -> presenter.onAddToPlanClicked(currentMeal));
    }

    @Override
    public void showConnectionError() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_connection);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void showGuestLoginDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_guest_login);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.findViewById(R.id.btn_login).setOnClickListener(v -> {
            dialog.dismiss();
            navigateToLogin();
        });

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void showWeekCalendarDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.week_calendar, null);
        bottomSheetDialog.setContentView(sheetView);

        RecyclerView rvWeekDays = sheetView.findViewById(R.id.rv_week_days);
        TextView tvCurrentMonth = sheetView.findViewById(R.id.tv_current_month);

        List<WeekCalendarAdapter.DayModel> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

        tvCurrentMonth.setText(monthFormat.format(calendar.getTime()));

        for (int i = 0; i < 7; i++) {
            days.add(new WeekCalendarAdapter.DayModel(
                    dayFormat.format(calendar.getTime()),
                    dateFormat.format(calendar.getTime()),
                    calendar.getTime().toString()
            ));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        selectedDateForPlan = null;

        WeekCalendarAdapter adapter = new WeekCalendarAdapter(days, selectedDay -> {
            try {
                selectedDateForPlan = new Date(selectedDay.getFullDate());
            } catch (Exception e) { e.printStackTrace(); }
        });

        rvWeekDays.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvWeekDays.setAdapter(adapter);

        sheetView.findViewById(R.id.btn_confirm_date).setOnClickListener(btn -> {
            if (selectedDateForPlan != null) {
                bottomSheetDialog.dismiss();
                showMealTypeSelectionDialog();
            } else {
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.please_select_a_date, Snackbar.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), R.string.please_select_a_date, Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private String extractYouTubeVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile(
                "(?:youtube\\.com/(?:watch\\?v=|embed/)|youtu\\.be/)([a-zA-Z0-9_-]{11})",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }

        if (youtubeUrl.contains("v=")) {
            String[] parts = youtubeUrl.split("v=");
            if (parts.length > 1) {
                String videoId = parts[1].split("&")[0];
                return videoId;
            }
        }

        return "";
    }

    @Override
    public void showMeal(Meal meal) {
        this.currentMeal = meal;
        mealTitle.setText(meal.strMeal);
        categoryChip.setText(meal.strCategory);
        areaChip.setText(meal.strArea);

        if (meal.isFavorite) {
            btnFavorite.setImageResource(R.drawable.favorite);
        } else {
            btnFavorite.setImageResource(R.drawable.unfavorite);
        }

        Glide.with(this)
                .load(meal.strMealThumb)
                .placeholder(R.drawable.medium)
                .error(R.drawable.medium)
                .into(mealImage);

        List<Pair<String, String>> ingredientList = meal.getIngredientsAndMeasures();
        int counter = ingredientList.size();

        itemNumber.setText(String.format(getString(R.string.d_item), counter));
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
            String videoId = extractYouTubeVideoId(meal.strYoutube);
            Log.d("MealDetails", "YouTube URL: " + meal.strYoutube);
            Log.d("MealDetails", "Extracted Video ID: " + videoId);

            if (!videoId.isEmpty()) {
                videoLabel.setVisibility(View.VISIBLE);
                videoCard.setVisibility(View.VISIBLE);
                youTubePlayerView.setVisibility(View.VISIBLE);

                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0);
                        Log.d("MealDetails", "YouTube player ready, video cued: " + videoId);
                    }
                });
            } else {
                videoLabel.setVisibility(View.GONE);
                videoCard.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.GONE);
            }
        } else {
            videoLabel.setVisibility(View.GONE);
            videoCard.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlanAddedSuccess() {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.success_added_to_plan, Snackbar.LENGTH_LONG).show();
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.mealDetailsFragment) {
                Navigation.findNavController(requireView()).navigate(R.id.action_mealDetailsFragment_to_plannerFragment);
            }
        }
    }

    @Override
    public void onPlanAddedError(String error) {
        if (getView() != null) {
            Snackbar.make(getView(), String.format(getString(R.string.failed_to_add_plan_s), error), Snackbar.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), String.format(getString(R.string.failed_to_add_plan_s), error), Toast.LENGTH_SHORT).show();
    }

    private void showMealTypeSelectionDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_meal_type);
        if(dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvDate = dialog.findViewById(R.id.tv_selected_date);
        ChipGroup chipGroup = dialog.findViewById(R.id.chip_group_meal_type);
        Button btnAdd = dialog.findViewById(R.id.btn_add_to_plan);

        Chip chipBreakfast = dialog.findViewById(R.id.chip_breakfast);
        Chip chipLunch = dialog.findViewById(R.id.chip_lunch);
        Chip chipDinner = dialog.findViewById(R.id.chip_dinner);

        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        tvDate.setText(displayFormat.format(selectedDateForPlan));

        updateChipVisuals(chipBreakfast, false);
        updateChipVisuals(chipLunch, false);
        updateChipVisuals(chipDinner, false);

        chipGroup.setSingleSelection(true);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateChipVisuals(chipBreakfast, false);
            updateChipVisuals(chipLunch, false);
            updateChipVisuals(chipDinner, false);

            String type = "";
            if (checkedId == R.id.chip_breakfast) {
                type = getString(R.string.breakfast);
                updateChipVisuals(chipBreakfast, true);
            }
            else if (checkedId == R.id.chip_lunch) {
                type = getString(R.string.lunch);
                updateChipVisuals(chipLunch, true);
            }
            else if (checkedId == R.id.chip_dinner) {
                type = getString(R.string.dinner);
                updateChipVisuals(chipDinner, true);
            }

            if(!type.isEmpty()){
                btnAdd.setText(String.format(getString(R.string.add_to_meal_type), type));
            } else {
                btnAdd.setText(R.string.add_to_plan);
            }
        });

        btnAdd.setOnClickListener(v -> {
            int selectedId = chipGroup.getCheckedChipId();
            String type = null;

            if (selectedId == R.id.chip_breakfast) type = "BREAKFAST";
            else if (selectedId == R.id.chip_lunch) type = "LUNCH";
            else if (selectedId == R.id.chip_dinner) type = "DINNER";

            if (type != null) {
                presenter.addToPlan(currentMeal, selectedDateForPlan, type);
                dialog.dismiss();
            } else {
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.select_meal, Snackbar.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), R.string.select_meal, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void updateChipVisuals(Chip chip, boolean isSelected) {
        if (chip == null) return;
        if (isSelected) {
            chip.setChipBackgroundColor(ColorStateList.valueOf(R.drawable.secondary_button_filled));
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_txt));
        } else {
            chip.setBackgroundResource(R.drawable.rounded_txt_field);
            chip.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.splash_subtitle_color));
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.txt_dark));
            chip.setChipBackgroundColor(null);
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
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }

    public void navigateToLogin() {
        Intent intent = new Intent(requireContext(), SplashActivity.class);
        intent.putExtra("IS_LOGOUT", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}