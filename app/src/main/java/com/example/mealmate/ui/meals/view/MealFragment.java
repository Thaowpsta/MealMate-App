package com.example.mealmate.ui.meals.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.meals.presenter.MealsPresenter;
import com.example.mealmate.ui.meals.presenter.MealsPresenterImp;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MealFragment extends Fragment implements MealsView{

    private MealsAdapter adapter;
    private MealsPresenter presenter;
    private final List<Meal> mealsList = new ArrayList<>();
    private TextView subtitle;

    public MealFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_meals);
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        subtitle = view.findViewById(R.id.meals_subtitle);

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MealsAdapter(mealsList);
        recyclerView.setAdapter(adapter);

        presenter = new MealsPresenterImp(this, getContext());

        if (getArguments() != null) {
            String categoryName = getArguments().getString("category_name");
            subtitle.setText(String.format(getString(R.string.browse_meals_by_s), categoryName));
            if (categoryName != null) {
                presenter.getMealsByCategory(categoryName);
            }
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMeals(List<Meal> meals) {
        mealsList.clear();
        mealsList.addAll(meals);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        if (getView() != null)
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}