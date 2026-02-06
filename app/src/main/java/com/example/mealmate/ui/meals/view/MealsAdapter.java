package com.example.mealmate.ui.meals.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.google.android.material.chip.Chip;

import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Meal> meals;

    public MealsAdapter(List<Meal> meals){
        this.meals = meals;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent, false);
            return new CardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Meal meal = meals.get(position);

            CardViewHolder cardHolder = (CardViewHolder) holder;

            cardHolder.title.setText(meal.strMeal);
            
            if (meal.strArea != null && !meal.strArea.isEmpty()) {
                cardHolder.chipArea.setVisibility(View.VISIBLE);
                cardHolder.chipArea.setText(meal.strArea);
            } else {
                cardHolder.chipArea.setVisibility(View.GONE);
            }

            if (meal.strCategory != null && !meal.strCategory.isEmpty()) {
                cardHolder.chipCategory.setVisibility(View.VISIBLE);
                cardHolder.chipCategory.setText(meal.strCategory);
            } else {
                cardHolder.chipCategory.setVisibility(View.GONE);
            }

            cardHolder.favoriteIcon.setVisibility(View.VISIBLE);
            cardHolder.favoriteIcon.setImageResource(meal.isFavorite? R.drawable.favorite : R.drawable.unfavorite);

            Glide.with(cardHolder.itemView.getContext())
                    .load(meal.strMealThumb)
                    .placeholder(R.drawable.medium)
                    .into(cardHolder.image);

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("meal", meal);
            Navigation.findNavController(v).navigate(R.id.action_mealFragment_to_mealDetailsFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
    
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public Chip chipArea;
        public Chip chipCategory;
        public ImageView favoriteIcon;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.mealImage1);
            title = itemView.findViewById(R.id.mealTitle1);
            chipArea = itemView.findViewById(R.id.meal_country);
            chipCategory = itemView.findViewById(R.id.meal_category);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon1);
        }
    }
}
