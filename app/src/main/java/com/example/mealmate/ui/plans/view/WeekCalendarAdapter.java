package com.example.mealmate.ui.plans.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.mealmate.R;

public class WeekCalendarAdapter extends RecyclerView.Adapter<WeekCalendarAdapter.DayViewHolder> {

    private final List<DayModel> days;
    private int selectedPosition = -1;
    private final OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(DayModel day);
    }

    public WeekCalendarAdapter(List<DayModel> days, OnDateSelectedListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayModel day = days.get(position);
        holder.tvDayName.setText(day.getDayName());
        holder.tvDayNumber.setText(day.getDayNumber());

        if (selectedPosition == position) {
            holder.container.setBackgroundResource(R.drawable.primary_button_filled);
            holder.container.setBackgroundTintList(null);

            holder.tvDayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.splash_subtitle_color));
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.splash_subtitle_color));
        } else {
            holder.container.setBackgroundResource(R.drawable.rounded_txt_field);
            holder.container.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.splash_subtitle_color));

            holder.tvDayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.txt_light));
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.txt_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousItem = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousItem);
            notifyItemChanged(selectedPosition);
            listener.onDateSelected(day);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber;
        View container;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tv_day_name);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            container = itemView.findViewById(R.id.item_day_container);
        }
    }

    public static class DayModel {
        private final String dayName;
        private final String dayNumber;
        private final String fullDate;

        public DayModel(String dayName, String dayNumber, String fullDate) {
            this.dayName = dayName;
            this.dayNumber = dayNumber;
            this.fullDate = fullDate;
        }

        public String getDayName() { return dayName; }
        public String getDayNumber() { return dayNumber; }
        public String getFullDate() { return fullDate; }
    }
}