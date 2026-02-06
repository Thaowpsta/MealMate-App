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

import java.text.MessageFormat;
import java.util.Objects;

public class PlannerAdapter extends ListAdapter<MealPlannerItem, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE_HEADER = 0;
    private static final int VIEW_TYPE_MEAL = 1;
    private static final int VIEW_TYPE_ADD_MEAL = 2;
    private static final int VIEW_TYPE_EMPTY_DAY = 3;

    private final OnPlannerActionClickListener listener;


    public PlannerAdapter(OnPlannerActionClickListener listener) {
        super(new MealPlannerDiffCallback());
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        MealPlannerItem item = getItem(position);
        if (item instanceof MealPlannerItem.DateHeader) return VIEW_TYPE_DATE_HEADER;
        if (item instanceof MealPlannerItem.MealItem) return VIEW_TYPE_MEAL;
        if (item instanceof MealPlannerItem.AddMealButton) return VIEW_TYPE_ADD_MEAL;
        if (item instanceof MealPlannerItem.EmptyDayPrompt) return VIEW_TYPE_EMPTY_DAY;
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
            case VIEW_TYPE_EMPTY_DAY:
                return new EmptyDayViewHolder(inflater.inflate(R.layout.item_empty_day, parent, false));
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
        } else if (holder instanceof EmptyDayViewHolder) {
            ((EmptyDayViewHolder) holder).bind((MealPlannerItem.EmptyDayPrompt) item, listener);
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
        private final TextView addText, promptText;

        public AddMealViewHolder(@NonNull View itemView) {
            super(itemView);
            addText = itemView.findViewById(R.id.addMealText);
            promptText = itemView.findViewById(R.id.promptText);
        }

        public void bind(MealPlannerItem.AddMealButton item, OnPlannerActionClickListener listener) {
            String mealTypeName = item.getMealType().name().toLowerCase();
            mealTypeName = Character.toUpperCase(mealTypeName.charAt(0)) + mealTypeName.substring(1);
            addText.setText(MessageFormat.format("Add {0}", mealTypeName));
            promptText.setText(item.getPromptText());
            itemView.setOnClickListener(v -> { if (listener != null) listener.onAddMealClick(item.getMealType()); });
        }
    }

    static class EmptyDayViewHolder extends RecyclerView.ViewHolder {
        private final TextView promptText;

        public EmptyDayViewHolder(@NonNull View itemView) {
            super(itemView);
            promptText = itemView.findViewById(R.id.emptyDayPrompt);
        }

        public void bind(MealPlannerItem.EmptyDayPrompt item, OnPlannerActionClickListener listener) {
            promptText.setText(item.getPromptText());
            itemView.setOnClickListener(v -> { if (listener != null) listener.onEmptyDayClick(); });
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
