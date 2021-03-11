package com.example.dyetr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchPlainText;
    private Button searchButton;
    private TextView statusTextView;
    private ListView foodListView;

    private String userId;
    private String meal;

    private RequestQueue requestQueue;

    private ArrayList<Food> foodList;
    private FoodListAdapter foodListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);
        Log.i(this.getLocalClassName(), "Activity created.");

        // get all views other than the food list
        searchPlainText = (EditText) findViewById(R.id.searchPlainText);
        searchButton = (Button) findViewById(R.id.searchButton);
        statusTextView = (TextView) findViewById(R.id.statusTextView);

        // set up the food list
        foodListView = (ListView) findViewById(R.id.foodListView);
        foodList = new ArrayList<Food>();
        foodListAdapter = new FoodListAdapter(getApplicationContext(), R.layout.food_layout, foodList);
        foodListView.setAdapter(foodListAdapter);

        // grab info from intent
        Intent intent = getIntent();

        userId = intent.getStringExtra("userId");
        meal = intent.getStringExtra("meal");

        // set up request queue with THE INTENTION OF STOPPING IT WHEN THE ACTIVITY RETURNS A FOOD
        requestQueue = Volley.newRequestQueue(this);

        // request recommendations and add them to foodListView
        getRecommendations(userId, meal);

        // set up functionality for "Go" button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFoodList();

                Log.i("click", "here");
                Context context = getApplicationContext();
                String searchQuery = searchPlainText.getText().toString();
                getSearchResults(searchQuery);
            }
        });

        // set up functionality for list items
        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food clickedFood = foodList.get(position);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", clickedFood.getName());
                resultIntent.putExtra("id", clickedFood.getId());
                resultIntent.putExtra("calories", clickedFood.getCalories());
                resultIntent.putExtra("carbohydrates", clickedFood.getCarbohydrates());
                resultIntent.putExtra("protein", clickedFood.getProtein());
                resultIntent.putExtra("fats", clickedFood.getFats());

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }

    private void getRecommendations(String userId, String meal) {
        String userIdEncoded = null;
        String mealEncoded = null;
        try {
            userIdEncoded = URLEncoder.encode(userId, "utf-8");
            mealEncoded = URLEncoder.encode(meal, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://10.0.2.2:5000/api/v1/recommend"
                + "?user_id=" + userIdEncoded
                + "&meal=" + mealEncoded;

        Log.i("URL", url);

        StringRequest recommendationRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    Log.i("FoodSearchActivity", response);
                    jsonObject = new JSONObject(response);

                    JSONArray foodsInfo = jsonObject.getJSONArray("recommendations");
                    for (int i = 0; i < foodsInfo.length(); i++) {
                        JSONObject foodInfo = foodsInfo.getJSONObject(i);

                        String name = foodInfo.getString("name");
                        String id = foodInfo.getString("id");
                        double calories = foodInfo.getDouble("calories");
                        double carbohydrates = foodInfo.getDouble("carbohydrates");
                        double protein = foodInfo.getDouble("protein");
                        double fats = foodInfo.getDouble("fat");

                        // create the food object and add it to the food list
                        Food food = new Food(id, name, calories, carbohydrates, protein, fats);
                        foodList.add(food);
                    }

                    foodListAdapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Recommendation.error", error.toString());
            }
        });

        Log.i("FoodSearchActivity", "Queueing request.");
        requestQueue.add(recommendationRequest);
    }

    private void getSearchResults(String searchQuery) {
        String searchQueryEncoded = null;
        try {
            searchQueryEncoded = URLEncoder.encode(searchQuery, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:5000/api/v1/search"
                + "?q=" + searchQueryEncoded;

        Log.i("URL", url);

        StringRequest searchRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    Log.i("FoodSearchActivity", response);
                    jsonObject = new JSONObject(response);

                    JSONArray foodsInfo = jsonObject.getJSONArray("foods");

                    if (foodsInfo.length() == 1) {
                        statusTextView.setText(String.valueOf(foodsInfo.length() + " result"));
                    }
                    else {
                        statusTextView.setText(String.valueOf(foodsInfo.length() + " results"));
                    }


                    for (int i = 0; i < foodsInfo.length(); i++) {
                        JSONObject foodInfo = foodsInfo.getJSONObject(i);

                        String name = foodInfo.getString("name");
                        String id = foodInfo.getString("id");
                        double calories = foodInfo.getDouble("calories");
                        double carbohydrates = foodInfo.getDouble("carbohydrates");
                        double protein = foodInfo.getDouble("protein");
                        double fats = foodInfo.getDouble("fat");

                        // create the food object and add it to the food list
                        Food food = new Food(id, name, calories, carbohydrates, protein, fats);
                        foodList.add(food);
                    }

                    foodListAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Search.error", error.toString());
            }
        });

        Log.i("FoodSearchActivity", "Queueing request.");
        requestQueue.add(searchRequest);
    }

    private void clearFoodList() {
        foodList.clear();
        foodListAdapter.notifyDataSetChanged();
    }
}