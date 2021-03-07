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
}