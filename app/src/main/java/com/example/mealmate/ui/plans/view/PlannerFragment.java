package com.example.mealmate.ui.plans.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.MealPlannerItem;
import com.example.mealmate.data.meals.models.MealType;
import com.example.mealmate.ui.plans.presenter.PlannerPresenterImp;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class PlannerFragment extends Fragment implements PlannerView {

    private RecyclerView recyclerView;
    private PlannerAdapter adapter;
    private TextView datePlan, mealsCount;
    private PlannerPresenterImp presenter;
    private Date currentViewDate;
    private Date minDate;
    private Date maxDate;
    private SimpleDateFormat displayFormat;
    private ImageView prevDayBtn, nextDayBtn;
    private GestureDetector gestureDetector;
    private boolean isAnimating = false;

    public PlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();

        minDate = removeTime(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 6);
        maxDate = removeTime(calendar.getTime());

        currentViewDate = minDate;
        displayFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backBtn = view.findViewById(R.id.btn_back);
        recyclerView = view.findViewById(R.id.rv_plans);
        datePlan = view.findViewById(R.id.date_plan);
        prevDayBtn = view.findViewById(R.id.imageView);
        nextDayBtn = view.findViewById(R.id.imageView1);
        mealsCount = view.findViewById(R.id.day_meal_count);

        presenter = new PlannerPresenterImp(this, requireContext());

        setupRecyclerView();
        updateDateView(false);

        gestureDetector = new GestureDetector(getContext(), new SwipeGestureListener());

        View.OnTouchListener touchListener = (v, event) -> gestureDetector.onTouchEvent(event);
        recyclerView.setOnTouchListener(touchListener);
        view.setOnTouchListener(touchListener);

        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        prevDayBtn.setOnClickListener(v -> changeDate(-1));
        nextDayBtn.setOnClickListener(v -> changeDate(1));

        datePlan.setOnClickListener(v -> showWeekCalendarDialog(MealType.BREAKFAST));
    }

    private void changeDate(int days) {
        if (isAnimating) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentViewDate);
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date newDate = cal.getTime();

        Date newDateNoTime = removeTime(newDate);

        if (newDateNoTime.before(minDate)) {
            if (getView() != null) {
                Snackbar.make(getView(), R.string.cannot_go_back_past_today, Snackbar.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(), R.string.cannot_go_back_past_today, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newDateNoTime.after(maxDate)) {
            if (getView() != null) {
                Snackbar.make(getView(), R.string.cannot_go_past_this_week, Snackbar.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(), R.string.cannot_go_past_this_week, Toast.LENGTH_SHORT).show();
            return;
        }

        currentViewDate = newDate;
        animateDateChange(days);
    }

    private void animateDateChange(int direction) {
        isAnimating = true;

        // Direction > 0 (Next Day): Slide out to Left (-TranslationX)
        // Direction < 0 (Prev Day): Slide out to Right (+TranslationX)
        float slideOutTo = (direction > 0) ? -recyclerView.getWidth() : recyclerView.getWidth();

        recyclerView.animate()
                .translationX(slideOutTo)
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    updateDateView(true);

                    float enterFrom = (direction > 0) ? recyclerView.getWidth() : -recyclerView.getWidth();
                    recyclerView.setTranslationX(enterFrom);

                    // Slide Back In
                    recyclerView.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(200)
                            .withEndAction(() -> isAnimating = false)
                            .start();
                })
                .start();
    }

    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void updateDateView(boolean dataOnly) {
        datePlan.setText(displayFormat.format(currentViewDate));

        if (!dataOnly) {
            updateNavigationButtons();
        } else {
            updateNavigationButtons();
        }

        if (presenter != null) {
            presenter.getMealsByDate(currentViewDate);
        }
    }

    private void updateNavigationButtons() {
        Date dateNoTime = removeTime(currentViewDate);

        if (dateNoTime.compareTo(minDate) <= 0) {
            prevDayBtn.setVisibility(View.INVISIBLE);
        } else {
            prevDayBtn.setVisibility(View.VISIBLE);
        }

        if (dateNoTime.compareTo(maxDate) >= 0) {
            nextDayBtn.setVisibility(View.INVISIBLE);
        } else {
            nextDayBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        adapter = new PlannerAdapter(new OnPlannerActionClickListener() {
            @Override
            public void onMealClick(MealPlannerItem.MealItem item) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("meal", item.getMeal());
                Navigation.findNavController(requireView()).navigate(R.id.action_plannerFragment_to_mealDetailsFragment, bundle);
            }

            @Override
            public void onAddMealClick(MealType mealType) {

                showWeekCalendarDialog(mealType);
            }

            @Override
            public void onEmptyDayClick() { }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void showWeekCalendarDialog(MealType mealType) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.week_calendar, null);
        bottomSheetDialog.setContentView(sheetView);

        RecyclerView rvWeekDays = sheetView.findViewById(R.id.rv_week_days);
        TextView tvCurrentMonth = sheetView.findViewById(R.id.tv_current_month);

        List<WeekCalendarAdapter.DayModel> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Show week starting from Today (MinDate)
        calendar.setTime(minDate);

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

        final AtomicReferenceArray<Date> selectedDate = new AtomicReferenceArray<>(new Date[]{currentViewDate});

        WeekCalendarAdapter calendarAdapter = new WeekCalendarAdapter(days, selectedDay -> {
            try {
                selectedDate.set(0, new Date(selectedDay.getFullDate()));
            } catch (Exception e) { e.printStackTrace(); }
        });

        rvWeekDays.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvWeekDays.setAdapter(calendarAdapter);

        sheetView.findViewById(R.id.btn_confirm_date).setOnClickListener(btn -> {
            bottomSheetDialog.dismiss();


            Bundle bundle = new Bundle();
            bundle.putBoolean("is_planning_mode", true);
            bundle.putLong("planned_date", currentViewDate.getTime());
            bundle.putString("planned_type", mealType.name());

            //TODO: nav to search to get meal then return to planned again

        });

        bottomSheetDialog.show();
    }

    @Override
    public void showPlans(List<MealPlannerItem> items) {
        adapter.submitList(items);
    }

    @Override
    public void showLoading() {}

    @Override
    public void hideLoading() {}

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDayMealCount(int count) {
        if(mealsCount != null) {
            String text = count == 1 ? getString(R.string._1_meal) : count + getString(R.string.meals);
            mealsCount.setText(text);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        changeDate(-1);
                    } else {
                        changeDate(1);
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}
