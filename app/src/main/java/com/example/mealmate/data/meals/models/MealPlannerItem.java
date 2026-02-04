package com.example.mealmate.data.meals.models;

import java.util.Objects;

public abstract class MealPlannerItem {

    public static class DateHeader extends MealPlannerItem {
        private String date;
        private int mealCount;

        public DateHeader(String date, int mealCount) {
            this.date = date;
            this.mealCount = mealCount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getMealCount() {
            return mealCount;
        }

        public void setMealCount(int mealCount) {
            this.mealCount = mealCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DateHeader that = (DateHeader) o;
            return mealCount == that.mealCount && Objects.equals(date, that.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, mealCount);
        }
    }

    public static class MealItem extends MealPlannerItem {
        private MealType mealType;
        private Meal meal;

        public MealItem(MealType mealType, Meal meal) {
            this.mealType = mealType;
            this.meal = meal;
        }

        public MealType getMealType() {
            return mealType;
        }

        public Meal getMeal() {
            return meal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MealItem mealItem = (MealItem) o;
            return mealType == mealItem.mealType && Objects.equals(meal, mealItem.meal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mealType, meal);
        }
    }

    public static class AddMealButton extends MealPlannerItem {
        private MealType mealType;
        private String promptText;

        public AddMealButton(MealType mealType, String promptText) {
            this.mealType = mealType;
            this.promptText = promptText;
        }

        public MealType getMealType() {
            return mealType;
        }

        public void setMealType(MealType mealType) {
            this.mealType = mealType;
        }

        public String getPromptText() {
            return promptText;
        }

        public void setPromptText(String promptText) {
            this.promptText = promptText;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AddMealButton that = (AddMealButton) o;
            return mealType == that.mealType && Objects.equals(promptText, that.promptText);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mealType, promptText);
        }
    }

    public static class EmptyDayPrompt extends MealPlannerItem {
        private String promptText;

        public EmptyDayPrompt(String promptText) {
            this.promptText = promptText;
        }

        public String getPromptText() {
            return promptText;
        }

        public void setPromptText(String promptText) {
            this.promptText = promptText;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmptyDayPrompt that = (EmptyDayPrompt) o;
            return Objects.equals(promptText, that.promptText);
        }

        @Override
        public int hashCode() {
            return Objects.hash(promptText);
        }
    }
}
