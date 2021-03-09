package com.example.dyetr;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchPlainText;
    private Button searchButton;
    private TextView weRecommendTextView;
    private ListView foodListView;

    private String testUserId;
    private String testMeal;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);
        Log.i(this.getLocalClassName(), "Activity created.");

        searchPlainText = (EditText) findViewById(R.id.searchPlainText);
        searchButton = (Button) findViewById(R.id.searchButton);
        weRecommendTextView = (TextView) findViewById(R.id.weRecommendTextView);
        foodListView = (ListView) findViewById(R.id.foodListView);

        testUserId = "e493433a-6e29-11eb-8884-0028f8f916b2";
        testMeal = "breakfast";

        // set up request queue with THE INTENTION OF STOPPING IT WHEN THE ACTIVITY RETURNS A FOOD
        requestQueue = Volley.newRequestQueue(this);

        // request recommendations and add them to foodListView
        getRecommendations(testUserId, testMeal);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "here");
                Context context = getApplicationContext();
                String text = searchPlainText.getText().toString();
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
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
        String url = "http://localhost:5000/api/v1/recommend"
                + "?user_id=" + userIdEncoded
                + "&meal=" + mealEncoded;

        StringRequest recommendationRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    Log.d("FoodSearchActivity", response);
                    jsonObject = new JSONObject(response);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Recommendation.error", error.toString());
            }
        });

        requestQueue.add(recommendationRequest);
    }
}