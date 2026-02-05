package com.example.mealmate.ui.favorites.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.favorites.presenter.FavoritesPresenterImp;

import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteView, OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private FavoritesPresenterImp presenter;
    private TextView favorites;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new FavoritesPresenterImp(this, requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_favorites);
        favorites = view.findViewById(R.id.fav_num);
        ImageButton btnBack = view.findViewById(R.id.btn_back);

        presenter.getFavorites();

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showFavorites(List<Meal> meals) {
        FavoritesAdapter adapter = new FavoritesAdapter(meals, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        String favText = meals.size() + " " + getString(R.string.favs_subtitle);
        favorites.setText(favText);
    }

    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onRemoveFavorite(Meal meal) {
        presenter.removeFavorite(meal);
    }

    @Override
    public void onMealClick(Meal meal) {
        FavoritesFragmentDirections.ActionFavoritesFragmentToMealDetailsFragment action = FavoritesFragmentDirections.actionFavoritesFragmentToMealDetailsFragment(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }
}
