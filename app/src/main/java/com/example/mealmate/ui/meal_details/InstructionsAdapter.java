package com.example.mealmate.ui.meal_details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mealmate.R;
import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.ViewHolder> {

    private final List<String> steps;
    private boolean isExpanded = false;

    public InstructionsAdapter(List<String> steps) {
        this.steps = steps;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        notifyDataSetChanged();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instruction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String step = steps.get(position);
        holder.stepNumber.setText(String.valueOf(position + 1));
        holder.content.setText(step);
    }

    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        if (isExpanded) {
            return steps.size();
        } else {
            return Math.min(steps.size(), 2);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumber, content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.tv_step_number);
            content = itemView.findViewById(R.id.tv_step_content);
        }
    }
}