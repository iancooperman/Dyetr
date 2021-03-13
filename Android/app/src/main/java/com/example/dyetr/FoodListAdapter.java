package com.example.dyetr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

// Adapter for the list of foods in FoodSearchActivity
public class FoodListAdapter extends ArrayAdapter<Food> {
    private Context context;
    int resource;

    public FoodListAdapter(@NonNull Context context, int resource, @NonNull List<Food> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String id = getItem(position).getId();
        String calories = String.valueOf(getItem(position).getCalories());
        String carbohydrates = String.valueOf(getItem(position).getCarbohydrates());
        String protein = String.valueOf(getItem(position).getProtein());
        String fats = String.valueOf(getItem(position).getFats());

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameFoodLayout);
        TextView caloriesTextView = (TextView) convertView.findViewById(R.id.caloriesFoodLayout);
        TextView carbohydratesTextView = (TextView) convertView.findViewById(R.id.carbohydratesFoodLayout);
        TextView proteinTextView = (TextView) convertView.findViewById(R.id.proteinFoodLayout);
        TextView fatsTextView = (TextView) convertView.findViewById(R.id.fatsFoodLayout);

        nameTextView.setText(name);
        caloriesTextView.setText(calories + " cal");
        carbohydratesTextView.setText(carbohydrates + " g");
        proteinTextView.setText(protein + " g");
        fatsTextView.setText(fats + " g");

        return convertView;
    }
}
