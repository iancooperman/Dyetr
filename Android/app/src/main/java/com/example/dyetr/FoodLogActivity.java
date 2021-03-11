package com.example.dyetr;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FoodLogActivity extends AppCompatActivity {

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
                updateBreakfastHeader(food);
                addFoodItem(food, mealTime);
            }
        });

        lunchAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get food search result
                Food food;
                MealTime mealTime = MealTime.LUNCH;
                updateLunchHeader(food);
                addFoodItem(food, mealTime);
            }
        });

        dinnerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get food search result
                Food food;
                MealTime mealTime = MealTime.DINNER;
                updateDinnerHeader(food);
                addFoodItem(food, mealTime);
            }
        });

    }

    private int getCaloriesEatenToday(){
        return 100;
        //API request
    }

    private int getCalorieGoal(){
        return 1000;
        //API request
    }

    private void addFoodItem(Food foodItem, MealTime mealTime){
        //MAKE API REQUEST
    }

    private void updateBreakfastHeader(Food foodItem){

    }

    private void updateLunchHeader(Food foodItem){

    }

    private void updateDinnerHeader(Food foodItem){

    }
}