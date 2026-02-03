package com.example.mealmate.ui.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.models.Category;
import com.example.mealmate.data.models.Meal;
import com.example.mealmate.ui.categories.CategoriesAdapter;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Meal> meals;

    public FavoritesAdapter(List<Meal> meals) {
        this.meals = meals;
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

        Glide.with(holder.itemView.getContext()).load(meal.strMealThumb).placeholder(R.drawable.medium).into(((CategoriesAdapter.CardViewHolder) holder).image);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}
