package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ConstraintLayout callSettings = (ConstraintLayout) findViewById(R.id.settingsItemLayout4);
        callSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(getApplicationContext(), ContactPickerActivity.class);
                startActivity(callIntent);
            }
        });
    }
}
