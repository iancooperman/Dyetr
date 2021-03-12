package com.example.dyetr;

// Utility enum representing the 3 traditional meals
public enum MealTime {
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner");

    private String name;

    private MealTime(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
