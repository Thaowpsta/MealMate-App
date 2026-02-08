package com.example.mealmate.data.meals.models;

public class FilterUIModel {
    private String name;
    private String imageUrl;
    private boolean isSelected;

    public FilterUIModel(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.isSelected = false;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}