package com.example.mealmate.ui.plans.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.model.Meal;
import com.example.mealmate.data.models.MealPlannerItem;
import com.example.mealmate.data.models.MealType;

import java.util.ArrayList;
import java.util.List;

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
        setupRecyclerView();
        loadSampleData();

        backBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new PlannerAdapter(new PlannerAdapter.OnPlannerActionClickListener() {
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

        // Monday, Oct 16
        items.add(new MealPlannerItem.DateHeader("Monday, Oct 16", 2));
        items.add(new MealPlannerItem.MealItem(
                MealType.BREAKFAST, new Meal()));
        items.add(new MealPlannerItem.AddMealButton(MealType.LUNCH, "Find a recipe or quick add"));
        items.add(new MealPlannerItem.MealItem(MealType.DINNER, new Meal()));

        // Tuesday, Oct 17
        items.add(new MealPlannerItem.DateHeader("Tuesday, Oct 17", 1));
        items.add(new MealPlannerItem.AddMealButton(MealType.BREAKFAST, "Find a recipe or quick add"));
        items.add(new MealPlannerItem.MealItem(MealType.LUNCH, new Meal()));
        items.add(new MealPlannerItem.AddMealButton(MealType.DINNER, "Find a recipe or quick add"));

        // Wednesday, Oct 18
        items.add(new MealPlannerItem.DateHeader("Wednesday, Oct 18", 0));
        items.add(new MealPlannerItem.EmptyDayPrompt("Tap to plan Wednesday"));

        adapter.submitList(items);
    }
}
