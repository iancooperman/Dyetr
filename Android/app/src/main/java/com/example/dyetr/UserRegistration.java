package com.example.dyetr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        Button joinNowBtn = (Button) findViewById(R.id.joinNowButton);

        // Button for submitting user info to backend
        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
                EditText lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
                EditText ageEditText = (EditText) findViewById(R.id.ageEditText);
                EditText weightEditText = (EditText) findViewById(R.id.weightEditText);
                EditText feetHeightEditText = (EditText) findViewById(R.id.feetHeightEditText);
                EditText inchesHeightEditText = (EditText) findViewById(R.id.inchesHeightEditText);
                TextView responseTextView = (TextView) findViewById(R.id.responseTextView);

                String name = firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString();
                int age = Integer.parseInt(ageEditText.getText().toString());
                int weight = Integer.parseInt(weightEditText.getText().toString());
                int heightFeet = Integer.parseInt(feetHeightEditText.getText().toString()) * 12;
                int heightInches = Integer.parseInt(inchesHeightEditText.getText().toString());

                // Calorie Goal: (10 * weight-in-kg + 6.25 * height-in-cm - 5 * age-in-years + 5) * 1.2 - 500
                int heightInCm = (int) ((heightFeet + heightInches) * 2.54);
                int weightInKg = (int) (weight * 0.45359237);
                int calorieGoal = (int) ((10 * weightInKg + 6.25 * heightInCm - 5 * age + 5) * 1.2 - 500);

                Submit(name, age, weight, calorieGoal, responseTextView);
            }
        });
    }

    // Perform the user info submittion
    private void Submit(String name, int age, int weight, int calorieGoal, TextView responseTextView){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String apiURL = " http://10.0.2.2:5000/api/v1/user/register";

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("age", age);
            jsonBody.put("weight", weight);
            jsonBody.put("calorie_goal", calorieGoal);

            // Request a string response from the /api/v1/user/register endpoint.
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, apiURL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Successfully added user to Neo4j database.
                        responseTextView.setText("Registration Successful");
                        Log.i("UserRegistration", response.toString());

                        String userId = "haha";
                        try {
                            userId = response.getString("user_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Off to the food log!
                        Intent intent = new Intent(getApplicationContext(), FoodLogActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         responseTextView.setText("Registration Unsuccessful. Error: " + error.toString());
                         Log.i("UserRegistration", error.toString());
                    }
                }
            );

            // Add the request to the RequestQueue.
            requestQueue.add(postRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}