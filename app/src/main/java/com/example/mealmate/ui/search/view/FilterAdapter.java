package com.example.mealmate.ui.search.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.FilterUIModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<FilterUIModel> items = new ArrayList<>();
    private final OnFilterClickListener listener;
    private boolean isMultiSelect = false;

    public interface OnFilterClickListener {
        void onFilterClick(List<String> selectedItems);
    }

    public FilterAdapter(OnFilterClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<FilterUIModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
        for (FilterUIModel item : items) {
            item.setSelected(false);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        FilterUIModel item = items.get(position);
        holder.chip.setText(item.getName());
        holder.chip.setChecked(item.isSelected());

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .circleCrop()
                    .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull android.graphics.drawable.Drawable resource, @androidx.annotation.Nullable com.bumptech.glide.request.transition.Transition<? super android.graphics.drawable.Drawable> transition) {
                            holder.chip.setChipIcon(resource);
                            holder.chip.setChipIconVisible(true);
                        }
                        @Override
                        public void onLoadCleared(@androidx.annotation.Nullable android.graphics.drawable.Drawable placeholder) {
                            holder.chip.setChipIcon(null);
                            holder.chip.setChipIconVisible(false);
                        }
                    });
        } else {
            holder.chip.setChipIcon(null);
            holder.chip.setChipIconVisible(false);
        }

        holder.itemView.setOnClickListener(v -> {
            android.util.Log.d("FilterAdapter", "Item clicked: " + item.getName());

            if (!isMultiSelect) {
                for (FilterUIModel m : items) {
                    m.setSelected(false);
                }
                item.setSelected(true);
            } else {
                item.setSelected(!item.isSelected());
            }
            notifyDataSetChanged();

            List<String> selectedNames = new ArrayList<>();
            for (FilterUIModel m : items) {
                if (m.isSelected()) {
                    selectedNames.add(m.getName());
                }
            }
            listener.onFilterClick(selectedNames);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.category_chip);
        }
    }
}