package com.example.dyetr;

// Utility class for storing food info
public class Food {
    private String id;
    private String name;
    private double calories;
    private double carbohydrates;
    private double protein;
    private double fats;


    Food(String id, String name, double calories, double carbohydrates, double protein, double fats) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fats = fats;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getProtein() {
        return protein;
    }

    public double getFats() {
        return fats;
    }
}
