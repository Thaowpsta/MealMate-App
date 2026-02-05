package com.example.mealmate.ui.categories.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.R;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.ui.categories.presenter.CategoriesPresenterImp;

import java.util.List;

public class CategoriesFragment extends Fragment implements CategoriesView {

    private RecyclerView rvAllCategories;
    private CategoriesPresenterImp presenter;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new CategoriesPresenterImp(this, requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAllCategories = view.findViewById(R.id.rv_all_categories);
        ImageButton btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        presenter.getCategories();
    }


    @Override
    public void showLoading() {
        rvAllCategories.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        rvAllCategories.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCategories(List<Category> categories) {
        CategoriesAdapter adapter = new CategoriesAdapter(categories, CategoriesAdapter.VIEW_TYPE_CARD);
        rvAllCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvAllCategories.setAdapter(adapter);
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