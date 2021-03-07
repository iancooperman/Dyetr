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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchPlainText;
    private Button searchButton;
    private TextView weRecommendTextView;
    private ListView foodListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);
        Log.i(this.getLocalClassName(), "Activity created.");

        searchPlainText = (EditText) findViewById(R.id.searchPlainText);
        searchButton = (Button) findViewById(R.id.searchButton);
        weRecommendTextView = (TextView) findViewById(R.id.weRecommendTextView);
        foodListView = (ListView) findViewById(R.id.foodListView);

        // request recommendations and add them to foodListView
        getRecommendations(user_id, meal);


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
        String url = "localhost:5000//api/v1/recommend"
                + "?user_id=" + userIdEncoded
                + "&meal=" + mealEncoded;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        })
    }
}