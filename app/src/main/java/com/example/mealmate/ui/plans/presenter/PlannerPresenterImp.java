package com.example.mealmate.ui.plans.presenter;

import android.content.Context;

import com.example.mealmate.data.meals.datasource.local.PlannedMealDTO;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.meals.models.MealPlannerItem;
import com.example.mealmate.data.meals.models.MealType;
import com.example.mealmate.data.repositories.MealRepository;
import com.example.mealmate.ui.plans.view.PlannerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlannerPresenterImp implements PlannerPresenter {

    private PlannerView view;
    private final MealRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public PlannerPresenterImp(PlannerView view, Context context) {
        this.view = view;
        this.repository = new MealRepository(context);
    }

    @Override
    public void getMealsByDate(Date date) {
        if (view != null) view.showLoading();

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = dbFormat.format(date);

        compositeDisposable.add(repository.getPlansByDate(dateStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dayPlans -> {
                            if (view != null) {
                                view.hideLoading();
                                List<MealPlannerItem> items = processPlansForDay(dayPlans);
                                view.showPlans(items);
                                view.showDayMealCount(dayPlans.size());
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            }
                        }
                )
        );
    }

    private List<MealPlannerItem> processPlansForDay(List<PlannedMealDTO> dayPlans) {
        List<MealPlannerItem> result = new ArrayList<>();

        // Map to check existing types efficiently
        Map<String, PlannedMealDTO> typeMap = new HashMap<>();
        if (dayPlans != null) {
            for (PlannedMealDTO p : dayPlans) {
                typeMap.put(p.mealType, p);
            }
        }

        // Always show the 3 slots for a single day view
        addMealOrButton(result, typeMap, "BREAKFAST", MealType.BREAKFAST);
        addMealOrButton(result, typeMap, "LUNCH", MealType.LUNCH);
        addMealOrButton(result, typeMap, "DINNER", MealType.DINNER);

        return result;
    }

    private void addMealOrButton(List<MealPlannerItem> result, Map<String, PlannedMealDTO> map, String key, MealType type) {
        if (map.containsKey(key)) {
            PlannedMealDTO plan = map.get(key);
            Meal meal = new Meal();
            meal.idMeal = plan.mealId;
            meal.strMeal = plan.mealName;
            meal.strMealThumb = plan.mealThumb;
            meal.strInstructions = "Planned for " + plan.date; // Minimal info
            result.add(new MealPlannerItem.MealItem(type, meal));
        } else {
             // Only add 'Add' button if desired. Mock data showed it.
             result.add(new MealPlannerItem.AddMealButton(type, "Add " + type.name().toLowerCase()));
        }
    }

    @Override
    public void onDestroy() {
        view = null;
        compositeDisposable.clear();
    }
}