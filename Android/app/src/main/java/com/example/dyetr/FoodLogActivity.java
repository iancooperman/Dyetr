package com.example.dyetr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FoodLogActivity extends AppCompatActivity {

    private String userId;

    private TextView caloriesEaten;
    private TextView totalCaloriesEaten;

    private TextView breakfastName;
    private TextView breakfastCalories;
    private Button breakfastAddButton;

    private TextView lunchName;
    private TextView lunchCalories;
    private Button lunchAddButton;

    private TextView dinnerName;
    private TextView dinnerCalories;
    private Button dinnerAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_log);

        // retrieve user id from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        //Header
        caloriesEaten = (TextView) findViewById(R.id.caloriesEatenTextView);
        totalCaloriesEaten =(TextView) findViewById(R.id.totalCaloriesTextView);

        //breakfast
        breakfastName = (TextView) findViewById(R.id.breakfastNameTextView);
        breakfastCalories = (TextView) findViewById(R.id.breakfastCaloriesTextView);
        breakfastAddButton = (Button) findViewById(R.id.breakfastAddButton);

        //lunch
        lunchName = (TextView) findViewById(R.id.lunchNameTextView);
        lunchCalories = (TextView) findViewById(R.id.lunchCaloriesTextView);
        lunchAddButton = (Button) findViewById(R.id.lunchAddButton);

        //dinner
        dinnerName = (TextView) findViewById(R.id.dinnerNameTextView);
        dinnerCalories = (TextView) findViewById(R.id.dinnerCaloriesTextView);
        dinnerAddButton = (Button) findViewById(R.id.dinnerAddButton);

        breakfastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get food search result
                Food food;
                MealTime mealTime = MealTime.BREAKFAST;

                Intent intent = new Intent(getApplicationContext(), FoodSearchActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("meal", "breakfast");

                startActivityForResult(intent, 1);

//                updateBreakfastHeader(food);
//                addFoodItem(food, mealTime);
            }
        });

        lunchAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get food search result
                Food food;
                MealTime mealTime = MealTime.LUNCH;

                Intent intent = new Intent(getApplicationContext(), FoodSearchActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("meal", "lunch");

                startActivityForResult(intent, 2);

//                updateLunchHeader(food);
//                addFoodItem(food, mealTime);
            }
        });

        dinnerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get food search result
                Food food;
                MealTime mealTime = MealTime.DINNER;

                Intent intent = new Intent(getApplicationContext(), FoodSearchActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("meal", "dinner");

                startActivityForResult(intent, 3);

//                updateDinnerHeader(food);
//                addFoodItem(food, mealTime);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String name = data.getStringExtra("name");
        String id = data.getStringExtra("id");
        double calories = data.getDoubleExtra("calories", 0);
        double carbohydrates = data.getDoubleExtra("carbohydrates", 0);
        double protein = data.getDoubleExtra("protein", 0);
        double fats = data.getDoubleExtra("fats", 0);

        Food food = new Food(id, name, calories, carbohydrates, protein, fats);

        switch (requestCode) {
            case 1:
                updateBreakfastHeader(food);
                addFoodItem(food, MealTime.BREAKFAST);
                break;

            case 2:
                updateLunchHeader(food);
                addFoodItem(food, MealTime.LUNCH);
                break;

            case 3:
                updateDinnerHeader(food);
                addFoodItem(food, MealTime.DINNER);
                break;
        }
    }

    private int getCaloriesEatenToday(){
        return 100;
        //API request
    }

    private int getCalorieGoal(){
        return 1000;
        //API request
    }

    private void addFoodItem(Food foodItem, MealTime mealTime) {
        //MAKE API REQUEST
    }

    private void updateBreakfastHeader(Food foodItem){
        breakfastName.setText(foodItem.getName());
        breakfastCalories.setText(String.valueOf(foodItem.getCalories()));
        breakfastAddButton.setEnabled(false);
    }

    private void updateLunchHeader(Food foodItem){
        lunchName.setText(foodItem.getName());
        lunchCalories.setText(String.valueOf(foodItem.getCalories()));
        lunchAddButton.setEnabled(false);
    }

    private void updateDinnerHeader(Food foodItem){
        dinnerName.setText(foodItem.getName());
        dinnerCalories.setText(String.valueOf(foodItem.getCalories()));
        dinnerAddButton.setEnabled(false);
    }
}