package com.example.mealmate.ui.plans.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.MealPlannerItem;
import com.example.mealmate.data.meals.models.MealType;

import java.util.Objects;

public class PlannerAdapter extends ListAdapter<MealPlannerItem, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MEAL = 1;
    private static final int VIEW_TYPE_ADD_MEAL = 2;

    private final OnPlannerActionClickListener listener;


    public PlannerAdapter(OnPlannerActionClickListener listener) {
        super(new MealPlannerDiffCallback());
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        MealPlannerItem item = getItem(position);
        if (item instanceof MealPlannerItem.MealItem) return VIEW_TYPE_MEAL;
        if (item instanceof MealPlannerItem.AddMealButton) return VIEW_TYPE_ADD_MEAL;
        throw new IllegalArgumentException("Unknown view type");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_MEAL:
                return new MealViewHolder(inflater.inflate(R.layout.item_plan, parent, false));
            case VIEW_TYPE_ADD_MEAL:
                return new AddMealViewHolder(inflater.inflate(R.layout.item_add_plan, parent, false));
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MealPlannerItem item = getItem(position);
        if (holder instanceof MealViewHolder) {
            ((MealViewHolder) holder).bind((MealPlannerItem.MealItem) item, listener);
        } else if (holder instanceof AddMealViewHolder) {
            ((AddMealViewHolder) holder).bind((MealPlannerItem.AddMealButton) item, listener);
        }
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealImage;
        private final TextView mealTypeText, mealNameText, mealInfoText;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealTypeText = itemView.findViewById(R.id.mealTypeText);
            mealNameText = itemView.findViewById(R.id.mealNameText);
            mealInfoText = itemView.findViewById(R.id.mealInfoText);
        }

        public void bind(MealPlannerItem.MealItem item, OnPlannerActionClickListener listener) {
            mealTypeText.setText(item.getMealType().name());
            mealNameText.setText(item.getMeal().getStrMeal());
            mealInfoText.setText(item.getMeal().getStrInstructions());
            Glide.with(itemView.getContext()).load(item.getMeal().getStrMealThumb()).into(mealImage);
            itemView.setOnClickListener(v -> { if (listener != null) listener.onMealClick(item); });
        }
    }

    static class AddMealViewHolder extends RecyclerView.ViewHolder {
        private final TextView addText;

        public AddMealViewHolder(@NonNull View itemView) {
            super(itemView);
            addText = itemView.findViewById(R.id.addMealText);
        }

        public void bind(MealPlannerItem.AddMealButton item, OnPlannerActionClickListener listener) {
            int typeResId;
            switch (item.getMealType()) {
                case BREAKFAST: typeResId = R.string.breakfast; break;
                case LUNCH: typeResId = R.string.lunch; break;
                case DINNER: typeResId = R.string.dinner; break;
                default: typeResId = R.string.meal_type;
            }
            String mealTypeName = itemView.getContext().getString(typeResId);

            String formattedText = itemView.getContext().getString(R.string.add_0);
            if (formattedText.contains("{0}")) {
                formattedText = formattedText.replace("{0}", mealTypeName);
            } else {
                formattedText = itemView.getContext().getString(R.string.add_to_meal_type, mealTypeName);
            }

            addText.setText(formattedText);
            itemView.setOnClickListener(v -> { if (listener != null) listener.onAddMealClick(item.getMealType()); });
        }
    }

    static class MealPlannerDiffCallback extends DiffUtil.ItemCallback<MealPlannerItem> {
        @Override
        public boolean areItemsTheSame(@NonNull MealPlannerItem oldItem, @NonNull MealPlannerItem newItem) {
            if (oldItem.getClass() != newItem.getClass()) return false;

            if (oldItem instanceof MealPlannerItem.DateHeader) {
                return Objects.equals(((MealPlannerItem.DateHeader) oldItem).getDate(), ((MealPlannerItem.DateHeader) newItem).getDate());
            }
            if (oldItem instanceof MealPlannerItem.MealItem) {
                MealPlannerItem.MealItem oldMeal = (MealPlannerItem.MealItem) oldItem;
                MealPlannerItem.MealItem newMeal = (MealPlannerItem.MealItem) newItem;
                return oldMeal.getMealType() == newMeal.getMealType() && Objects.equals(oldMeal.getMeal().getStrMeal(), newMeal.getMeal().getStrMeal());
            }
            if (oldItem instanceof MealPlannerItem.AddMealButton) {
                return ((MealPlannerItem.AddMealButton) oldItem).getMealType() == ((MealPlannerItem.AddMealButton) newItem).getMealType();
            }
            return oldItem instanceof MealPlannerItem.EmptyDayPrompt;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MealPlannerItem oldItem, @NonNull MealPlannerItem newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }
}