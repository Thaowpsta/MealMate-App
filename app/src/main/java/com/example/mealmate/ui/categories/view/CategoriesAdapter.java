package com.example.mealmate.ui.categories.view;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mealmate.R;
import com.example.mealmate.data.categories.model.Category;
import com.google.android.material.chip.Chip;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_CHIP = 0;
    public static final int VIEW_TYPE_CARD = 1;

    private final List<Category> categories;
    private final int viewType;
    private final OnCategoryClickListener onCategoryClickListener;

    public CategoriesAdapter(List<Category> categories, int viewType, OnCategoryClickListener listener){
        this.categories = categories;
        this.viewType = viewType;
        onCategoryClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CARD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent, false);
            return new CardViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ChipViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category);
            }
        });

        if (holder instanceof ChipViewHolder) {
            ChipViewHolder chipHolder = (ChipViewHolder) holder;
            chipHolder.chip.setText(category.strCategory);

            Glide.with(chipHolder.itemView.getContext())
                    .asDrawable()
                    .load(category.strCategoryThumb)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            chipHolder.chip.setChipIcon(resource);
                            chipHolder.chip.setChipIconVisible(true);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            chipHolder.chip.setChipIcon(null);
                            chipHolder.chip.setChipIconVisible(false);
                        }
                    });
        } else if (holder instanceof CardViewHolder) {
            CardViewHolder cardHolder = (CardViewHolder) holder;

            cardHolder.title.setText(category.strCategory);
            cardHolder.chipArea.setVisibility(View.GONE);
            cardHolder.chipCategory.setVisibility(View.GONE);
            cardHolder.favoriteIcon.setVisibility(View.GONE);

            Glide.with(cardHolder.itemView.getContext())
                    .load(category.strCategoryThumb)
                    .placeholder(R.drawable.plate)
                    .into(cardHolder.image);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ChipViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public ChipViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.category_chip);
        }
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