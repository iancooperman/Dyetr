package com.example.dyetr;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Calendar;

public class FoodLogActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String userId;

    private TextView caloriesEaten;
    private TextView totalCalories;

    private TextView breakfastName;
    private TextView breakfastCalories;
    private Button breakfastAddButton;

    private TextView lunchName;
    private TextView lunchCalories;
    private Button lunchAddButton;

    private TextView dinnerName;
    private TextView dinnerCalories;
    private Button dinnerAddButton;

    private TextView dateText;
    private Button changeDateButton;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_log);

        // retrieve user id from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        //Header
        caloriesEaten = (TextView) findViewById(R.id.caloriesEatenTextView);
        totalCalories =(TextView) findViewById(R.id.totalCaloriesTextView);

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

        //date
        dateText = (TextView) findViewById(R.id.dateTextView);
        changeDateButton = (Button) findViewById(R.id.changeDateButton);
        Calendar c = Calendar.getInstance();
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        dateText.setText(currentDateString);

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

        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // set calorie fields
//        getCaloriesEatenToday();
        getCalorieGoal(userId);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        dateText.setText(currentDateString);
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

    private void getCalorieGoal(String userId){
        String userIdEncoded = null;

        try {
            userIdEncoded = URLEncoder.encode(userId, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:5000/api/v1/user"
                + "?id=" + userIdEncoded;

        Log.i("URL", url);

        StringRequest userRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    Log.i("FoodLogActivity", response);
                    jsonObject = new JSONObject(response);

                    int calorieGoal = jsonObject.getInt("calorie_goal");
                    totalCalories.setText(Integer.toString(calorieGoal));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("User.error", error.toString());
            }
        });

        requestQueue.add(userRequest);
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