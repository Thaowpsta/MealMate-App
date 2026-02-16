package com.example.mealmate.ui.home.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.meals.datasource.local.PlannedMealDTO;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.categories.view.CategoriesAdapter;
import com.example.mealmate.ui.home.presenter.HomePresenter;
import com.example.mealmate.ui.home.presenter.HomePresenterImp;
import com.example.mealmate.ui.plans.view.WeekCalendarAdapter;
import com.example.mealmate.ui.splash.view.SplashActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements HomeView {

    private HomePresenter presenter;
    private TextView mealTitle, favNum, plannedNum, planTitle, planType;
    private ImageView mealImage, planImage;
    private Chip areaChip, categoryChip;
    private ImageButton refreshButton;
    private UserRepository userRepository;
    private Meal currentMeal;
    private RecyclerView rvCategories;
    private LottieAnimationView loadingAnimation;
    private String cacheDateKey;

    private Date selectedDateForPlan;
    private CardView plansCard, todayPlan;
    private View emptyPlanView;;
    private PlannedMealDTO todaysPlanDTO;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(requireContext());
        presenter = new HomePresenterImp(this, requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingAnimation = view.findViewById(R.id.animation_view);
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
        favNum = view.findViewById(R.id.fav_num);
        plannedNum = view.findViewById(R.id.planed_num);
        Button btnCookNow = view.findViewById(R.id.cook_now);
        Button btnCookLater = view.findViewById(R.id.cook_later);
        CardView favoritesCard = view.findViewById(R.id.fav_card);
        plansCard = view.findViewById(R.id.planner_card);
        planImage = view.findViewById(R.id.plan_meal_image);
        planTitle = view.findViewById(R.id.plan_meal_title);
        planType = view.findViewById(R.id.mealTypeText);
        todayPlan = view.findViewById(R.id.plan_card);
        emptyPlanView = view.findViewById(R.id.empty_plan_view);

        Date today = new Date();

        // 1. Format for UI
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMM. d", Locale.getDefault());
        String displayDate = displayFormat.format(today);
        date.setText(displayDate);

        // 2. Format for Cache
        SimpleDateFormat cacheFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        cacheDateKey = cacheFormat.format(today);

        setupUserInfo(usernameTxt, userImg);

        presenter.getCachedMeal(cacheDateKey);
        presenter.getCategories();
        presenter.getFavoritesCount();
        presenter.getPlansCount();
        presenter.getTodaysPlan();

        refreshButton.setOnClickListener(v -> presenter.getRandomMeal());
        logoutButton.setOnClickListener(v -> presenter.logout());

        modCard.setOnClickListener(v -> {if (currentMeal != null) {presenter.onMealClicked(currentMeal);}});

        todayPlan.setOnClickListener(v -> {
            if (todaysPlanDTO != null) {
                Meal meal = new Meal();
                meal.idMeal = todaysPlanDTO.mealId;
                meal.strMeal = todaysPlanDTO.mealName;
                meal.strMealThumb = todaysPlanDTO.mealThumb;
                meal.strInstructions = String.format(getString(R.string.planned_for_s), todaysPlanDTO.date);
                presenter.onMealClicked(meal);
            }
        });

        if (emptyPlanView != null) {
            emptyPlanView.setOnClickListener(v -> {
                if(userRepository.isGuest()) showGuestLoginDialog();
                else Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_plannerFragment);
            });
        }

        seeAll.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        seeAll.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_categoriesFragment));

        btnCookNow.setOnClickListener(v -> {
            if(userRepository.isGuest()) {
                showGuestLoginDialog();
            } else if (currentMeal != null) {
                showCookNowConfirmation(currentMeal);
            }
        });

        btnCookLater.setOnClickListener(v -> presenter.onCookLaterClicked(currentMeal));

        favoritesCard.setOnClickListener(v -> {
            if(userRepository.isGuest()) showGuestLoginDialog();
            else Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_favoritesFragment);
        });

        plansCard.setOnClickListener(v -> {
            if(userRepository.isGuest()) showGuestLoginDialog();
            else Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_plannerFragment);
        });

        userImg.setOnClickListener(v -> {
            if(userRepository.isGuest()) showGuestLoginDialog();
            else Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_profileFragment);
        });
    }

    private void showCookNowConfirmation(Meal meal) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String type;
        if (hour >= 5 && hour < 11) {
            type = getString(R.string.breakfast);
        } else if (hour >= 11 && hour < 16) {
            type = getString(R.string.lunch);
        } else {
            type = getString(R.string.dinner);
        }

        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        String displayDate = displayFormat.format(cal.getTime());

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cook_now_confirmation);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView ivMealPreview = dialog.findViewById(R.id.iv_meal_preview);
        TextView tvMealName = dialog.findViewById(R.id.tv_meal_name);
        TextView tvMealTypePreview = dialog.findViewById(R.id.tv_meal_type_preview);
        TextView tvScheduleDate = dialog.findViewById(R.id.tv_schedule_date);
        TextView tvScheduleMealType = dialog.findViewById(R.id.tv_schedule_meal_type);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        tvMealName.setText(meal.strMeal);
        tvMealTypePreview.setText(type);
        tvScheduleDate.setText(String.format("Today, %s", displayDate));
        tvScheduleMealType.setText(type);

        if (meal.strMealThumb != null && !meal.strMealThumb.isEmpty()) {
            Glide.with(this)
                    .load(meal.strMealThumb)
                    .error(R.drawable.plate)
                    .centerCrop()
                    .into(ivMealPreview);
        }

        btnConfirm.setOnClickListener(v -> {
            presenter.onCookNowClicked(meal);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
            presenter.logout();
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

    private void setupUserInfo(TextView usernameTxt, ImageView userImg) {
        if (userRepository.isUserLoggedIn()) {
            String name = userRepository.getUserDisplayName();
            String email = userRepository.getUserEmail();
            if (name == null || name.isEmpty()) {
                name = (email != null && email.contains("@")) ? email.split("@")[0] : String.valueOf(R.string.guest);
            }
            usernameTxt.setText(name);
            String photoUrl = userRepository.getUserPhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this).load(photoUrl).placeholder(R.drawable.user).circleCrop().into(userImg);
            }
        } else {
            usernameTxt.setText(R.string.guest);
        }
    }

    @Override
    public void showLoading() {
        refreshButton.setEnabled(false);
        mealTitle.setText(R.string.loading);
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void hideLoading() {
        refreshButton.setEnabled(true);
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.GONE);
        }
    }
    @Override
    public void showMeal(Meal meal) {
        currentMeal = meal;
        mealTitle.setText(meal.strMeal);
        categoryChip.setText(meal.strCategory);
        areaChip.setText(meal.strArea);

        String mealJson = new Gson().toJson(meal);
        userRepository.saveCachedMeal(mealJson, cacheDateKey);

        Glide.with(this).load(meal.strMealThumb).error(R.drawable.plate).into(mealImage);
    }

    @Override
    public void showTodaysPlan(PlannedMealDTO plan) {
        this.todaysPlanDTO = plan;

        if (plan != null) {
            if (todayPlan != null) todayPlan.setVisibility(View.VISIBLE);
            if (emptyPlanView != null) emptyPlanView.setVisibility(View.GONE);

            if (planTitle != null) planTitle.setText(plan.mealName);
            if (planType != null) {
                planType.setText(plan.mealType);
                planType.setVisibility(View.VISIBLE);
            }
            if (planImage != null) {
                Glide.with(this)
                        .load(plan.mealThumb)
                        .error(R.drawable.plate)
                        .centerCrop()
                        .into(planImage);
            }
        } else {
            if (todayPlan != null) todayPlan.setVisibility(View.GONE);
            if (emptyPlanView != null) emptyPlanView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void navigateToMealDetails(Meal meal) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("meal", meal);
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_mealDetailsFragment, bundle);
    }

    @Override
    public void showCategories(List<Category> categories) {
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories, CategoriesAdapter.VIEW_TYPE_CHIP, category -> {

            Bundle bundle = new Bundle();
            bundle.putString("category_name", category.strCategory);
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_mealFragment, bundle);

        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoriesAdapter);
    }

    @Override
    public void showFavoritesCount(int count) {
        favNum.setText(String.valueOf(count));
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
    public void onPlanAddedSuccess() {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.success_added_to_plan, Snackbar.LENGTH_LONG).show();
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.homeFragment) {
                Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_plannerFragment);
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

    @Override
    public void showPlansCount(int count) {
        if(plannedNum != null) {
            plannedNum.setText(String.valueOf(count));
        }
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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