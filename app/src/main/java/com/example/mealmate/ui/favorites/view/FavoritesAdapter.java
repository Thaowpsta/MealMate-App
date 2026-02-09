package com.example.mealmate.ui.favorites.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.categories.view.CategoriesAdapter;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Meal> meals;
    private final OnFavoriteClickListener favoriteClickListener;

    public FavoritesAdapter(List<Meal> meals, OnFavoriteClickListener listener) {
        this.meals = meals;
        favoriteClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent, false);
        return new CategoriesAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Meal meal = meals.get(position);

        CategoriesAdapter.CardViewHolder cardViewHolder = (CategoriesAdapter.CardViewHolder) holder;

        cardViewHolder.title.setText(meal.strCategory);
        cardViewHolder.chipArea.setText(meal.strArea);
        cardViewHolder.chipCategory.setText(meal.strCategory);

        Glide.with(holder.itemView.getContext()).load(meal.strMealThumb).placeholder(R.drawable.plate).into(((CategoriesAdapter.CardViewHolder) holder).image);

        cardViewHolder.itemView.setOnClickListener(v -> favoriteClickListener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}
