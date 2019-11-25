package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.MapView;

public class coffeeSuggestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_suggestions);

        MapView coffeeMap = findViewById(R.id.mapView);
    }
}
