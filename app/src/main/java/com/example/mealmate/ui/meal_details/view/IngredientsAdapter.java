package com.example.mealmate.ui.meal_details.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import java.util.List;
import androidx.core.util.Pair;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private final List<Pair<String, String>> ingredients; // Pair of Name and Measure

    public IngredientsAdapter(List<Pair<String, String>> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, String> item = ingredients.get(position);
        String name = item.first;
        String measure = item.second;

        holder.name.setText(name);
        holder.measure.setText(measure);

        String imgUrl = "https://www.themealdb.com/images/ingredients/" + name + "-Small.png";
        Glide.with(holder.itemView.getContext()).load(imgUrl).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, measure;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_ingredient_name);
            measure = itemView.findViewById(R.id.tv_ingredient_measure);
            image = itemView.findViewById(R.id.img_ingredient_thumb);
        }
    }
}