package com.example.mealmate.ui.plans.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.meals.models.MealPlannerItem;
import com.example.mealmate.data.meals.models.MealType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlannerFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlannerAdapter adapter;
    private ImageButton backBtn;

    public PlannerFragment() {
        // Required empty public constructor
    }

    public static PlannerFragment newInstance() {
        return new PlannerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = view.findViewById(R.id.btn_back);
        recyclerView = view.findViewById(R.id.rv_plans);
        TextView datePlan = view.findViewById(R.id.date_plan);

        setupRecyclerView();
        loadSampleData();

        backBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());
        datePlan.setOnClickListener(v -> showWeekCalendarDialog());
    }

    private void showWeekCalendarDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
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

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        for (int i = 0; i < 7; i++) {
            days.add(new WeekCalendarAdapter.DayModel(
                    dayFormat.format(calendar.getTime()),
                    dateFormat.format(calendar.getTime()),
                    calendar.getTime().toString()
            ));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        WeekCalendarAdapter adapter = new WeekCalendarAdapter(days, selectedDay -> {
            //TODO: send date to db and add plan
        });

        rvWeekDays.setAdapter(adapter);

        sheetView.findViewById(R.id.btn_confirm_date).setOnClickListener(btn -> {
            // TODO: getFullDate
            bottomSheetDialog.dismiss();
        });

        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow().setWindowAnimations(R.style.CalendarDialogAnimation);
        }

            bottomSheetDialog.show();
    }
    private void setupRecyclerView() {
        adapter = new PlannerAdapter(new OnPlannerActionClickListener() {
            @Override
            public void onMealClick(MealPlannerItem.MealItem meal) {
                // TODO: Handle meal click
            }

            @Override
            public void onAddMealClick(MealType mealType) {
                // TODO: Handle add meal click
            }

            @Override
            public void onEmptyDayClick() {
                // TODO: Handle empty day click
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadSampleData() {
        List<MealPlannerItem> items = new ArrayList<>();

        // 1
        items.add(new MealPlannerItem.DateHeader("Monday, Oct 16", 2));
        items.add(new MealPlannerItem.MealItem(
                MealType.BREAKFAST, createMockMeal("Pancakes", "https://www.themealdb.com/images/media/meals/rwuyqx1511383174.jpg")));
        items.add(new MealPlannerItem.AddMealButton(MealType.LUNCH, "Find a recipe or quick add"));
        items.add(new MealPlannerItem.MealItem(MealType.DINNER, createMockMeal("Spaghetti Carbonara", "https://www.themealdb.com/images/media/meals/llc9u11574488887.jpg")));

        // 2
        items.add(new MealPlannerItem.DateHeader("Tuesday, Oct 17", 1));
        items.add(new MealPlannerItem.AddMealButton(MealType.BREAKFAST, "Find a recipe or quick add"));
        items.add(new MealPlannerItem.MealItem(MealType.LUNCH, createMockMeal("Chicken Caesar Salad", "https://www.themealdb.com/images/media/meals/syqypv1486981727.jpg")));
        items.add(new MealPlannerItem.AddMealButton(MealType.DINNER, "Find a recipe or quick add"));

        // 3
        items.add(new MealPlannerItem.DateHeader("Wednesday, Oct 18", 0));
        items.add(new MealPlannerItem.EmptyDayPrompt("Tap to plan Wednesday"));

        adapter.submitList(items);
    }

    private Meal createMockMeal(String name, String thumb) {
        Meal meal = new Meal();
        meal.strMeal = name;
        meal.strMealThumb = thumb;
        meal.strCategory = "Mock Category";
        return meal;
    }
}
