package com.example.dyetr;

import android.annotation.SuppressLint;
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

import org.json.JSONArray;
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

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_log);

        // initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // retrieve user id from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        // set calendar and food by date
        calendar = Calendar.getInstance();
        loadFoodsByDate(userId, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        //Calorie Info
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

        // set calorie fields
        getCalorieGoal(userId);
        calculateCaloriesEaten();

        // send users to FoodSearchActivity to pick a food to set as breakfast
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

        // send users to FoodSearchActivity to pick a food to set as lunch
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

            }
        });

        // send users to FoodSearchActivity to pick a food to set as dinner
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
            }
        });

        // allow users to change the date they're logging food for
        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    // Update date text and load appropriate eaten foods after user chooses a date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());
        dateText.setText(currentDateString);

        loadFoodsByDate(userId, year, month + 1, dayOfMonth);
    }

    // Utility function for retrieving eaten foods from the backend
    private void loadFoodsByDate(String userId, int year, int month, int day) {
        String userIdEncoded = null;
        String yearEncoded = null;
        String monthEncoded = null;
        String dayEncoded = null;
        try {
            userIdEncoded = URLEncoder.encode(userId, "utf-8");
            yearEncoded = URLEncoder.encode(Integer.toString(year), "utf-8");
            monthEncoded = URLEncoder.encode(Integer.toString(month), "utf-8");
            dayEncoded = URLEncoder.encode(Integer.toString(day), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:5000/api/v1/food_eaten"
                + "?user_id=" + userIdEncoded
                + "&year=" + yearEncoded
                + "&month=" + monthEncoded
                + "&day=" + dayEncoded;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Food eaten", response);

                // clear screen of any foods on it
                clearFoods();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        JSONObject foodJson = result.getJSONObject("food");
                        String name = foodJson.getString("name");
                        String id = foodJson.getString("id");
                        double calories = foodJson.getDouble("calories");
                        double carbohydrates = foodJson.getDouble("carbohydrates");
                        double protein = foodJson.getDouble("protein");
                        double fats = foodJson.getDouble("fat");

                        Food food = new Food(id, name, calories, carbohydrates, protein, fats);
                        String meal = result.getString("meal");

                        switch (meal) {
                            case "breakfast":
                                updateBreakfastHeader(food);
                                break;
                            case "lunch":
                                updateLunchHeader(food);
                                break;
                            case "dinner":
                                updateDinnerHeader(food);
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Food eaten.error", error.toString());
            }
        });

        requestQueue.add(request);
    }

    // Clear the activity of logged foods (used when changing dates)
    private void clearFoods() {
        breakfastName.setText("");
        breakfastCalories.setText("");
        breakfastAddButton.setEnabled(true);

        lunchName.setText("");
        lunchCalories.setText("");
        lunchAddButton.setEnabled(true);

        dinnerName.setText("");
        dinnerCalories.setText("");
        dinnerAddButton.setEnabled(true);

        calculateCaloriesEaten();
    }

    // Calculate the number of calories eaten on the set date and update the appropriate Text View
    @SuppressLint("SetTextI18n")
    private void calculateCaloriesEaten() {
        String breakfastString = (String) breakfastCalories.getText();
        String lunchString = (String) lunchCalories.getText();
        String dinnerString = (String) dinnerCalories.getText();

        double breakfastAmount = (breakfastString.equals("")) ? 0 : Double.parseDouble(breakfastString);
        double lunchAmount = (lunchString.equals("")) ? 0 : Double.parseDouble(lunchString);
        double dinnerAmount = (dinnerString.equals("")) ? 0 : Double.parseDouble(dinnerString);

        int total = (int) (breakfastAmount + lunchAmount + dinnerAmount);
        caloriesEaten.setText(Integer.toString(total));
    }

    @Override
    // After returning from the FoodSearchActivity with food, update the appropriate Text Views
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String name = data.getStringExtra("name");
        String id = data.getStringExtra("id");
        double calories = data.getDoubleExtra("calories", 0);
        double carbohydrates = data.getDoubleExtra("carbohydrates", 0);
        double protein = data.getDoubleExtra("protein", 0);
        double fats = data.getDoubleExtra("fats", 0);

        Food food = new Food(id, name, calories, carbohydrates, protein, fats);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i("Year", String.valueOf(year));
        Log.i("month", String.valueOf(month));
        Log.i("day", String.valueOf(day));

        switch (requestCode) {
            case 1:
                updateBreakfastHeader(food);
                addFoodItem(userId, food.getId(), "breakfast", year, month, day);
                break;

            case 2:
                updateLunchHeader(food);
                addFoodItem(userId, food.getId(), "lunch", year, month, day);
                break;

            case 3:
                updateDinnerHeader(food);
                addFoodItem(userId, food.getId(), "dinner", year, month, day);
                break;
        }
    }

    // Retrieve a user's calorie goal from the backend and update the appropriate Text Views
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

    // log a food as eaten in the backend
    private void addFoodItem(String userId, String foodId, String mealType, int year, int month, int day) {
        String userIdEncoded = null;
        String foodIdEncoded = null;
        String mealTypeEncoded = null;
        String yearEncoded = null;
        String monthEncoded = null;
        String dayEncoded = null;
        try {
            userIdEncoded = URLEncoder.encode(userId, "utf-8");
            foodIdEncoded = URLEncoder.encode(foodId, "utf-8");
            mealTypeEncoded = URLEncoder.encode(mealType, "utf-8");
            yearEncoded = URLEncoder.encode(Integer.toString(year), "utf-8");
            monthEncoded = URLEncoder.encode(Integer.toString(month), "utf-8");
            dayEncoded = URLEncoder.encode(Integer.toString(day), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:5000/api/v1/food_eaten"
                + "?user_id=" + userIdEncoded
                + "&food_id=" + foodIdEncoded
                + "&meal_type=" + mealTypeEncoded
                + "&year=" + yearEncoded
                + "&month=" + monthEncoded
                + "&day=" + dayEncoded;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("FoodLogActivity", "Food added");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Food eaten.error", error.toString());
            }
        });

        requestQueue.add(request);
    }

    // Utility function for displaying a breakfast food
    private void updateBreakfastHeader(Food foodItem){
        breakfastName.setText(foodItem.getName());
        breakfastCalories.setText(String.valueOf(foodItem.getCalories()));
        breakfastAddButton.setEnabled(false);

        calculateCaloriesEaten();
    }

    // Utility function for displaying a lunch food
    private void updateLunchHeader(Food foodItem){
        lunchName.setText(foodItem.getName());
        lunchCalories.setText(String.valueOf(foodItem.getCalories()));
        lunchAddButton.setEnabled(false);

        calculateCaloriesEaten();
    }

    // Utility function for displaying a lunch food
    private void updateDinnerHeader(Food foodItem){
        dinnerName.setText(foodItem.getName());
        dinnerCalories.setText(String.valueOf(foodItem.getCalories()));
        dinnerAddButton.setEnabled(false);

        calculateCaloriesEaten();
    }
}