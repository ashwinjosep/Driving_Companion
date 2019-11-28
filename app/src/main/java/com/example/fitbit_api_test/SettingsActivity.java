package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.example.fitbit_api_test.utils.PrefsHelper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadSettingsValues();

        //handle driving switch logic
        final Switch isDrivingSwitch = findViewById(R.id.switch1);
        isDrivingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrivingSwitch.isChecked())
                {
                    PrefsHelper.setSwitchValue("drivingSwitch", true);
                    Log.d("driving switch", "checked");
                }
                    else
                {
                    PrefsHelper.setSwitchValue("drivingSwitch", false);
                    Log.d("driving switch", "not checked");
                }
            }
        });

        //handle driving switch logic
        final Switch heartAttackSwitch = findViewById(R.id.switch2);
        heartAttackSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heartAttackSwitch.isChecked())
                {
                    PrefsHelper.setSwitchValue("heartAttackSwitch", true);
                    Log.d("heart attack switch", "checked");
                }
                else
                {
                    PrefsHelper.setSwitchValue("heartAttackSwitch", false);
                    Log.d("heart attack switch", "not checked");
                }
            }
        });

        //handle driving switch logic
        final Switch isDrowsySwitch = findViewById(R.id.switch3);
        isDrowsySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrowsySwitch.isChecked())
                {
                    PrefsHelper.setSwitchValue("drowsySwitch", true);
                    Log.d("drowsy switch", "checked");
                }
                else
                {
                    PrefsHelper.setSwitchValue("drowsySwitch", false);
                    Log.d("drowsy switch", "not checked");
                }
            }
        });

        ConstraintLayout callSettings = (ConstraintLayout) findViewById(R.id.settingsItemLayout4);
        callSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(getApplicationContext(), ContactPickerActivity.class);
                startActivity(callIntent);
            }
        });
    }

    private void loadSettingsValues() {

        //driving switch value
        final Switch isDrivingSwitch = findViewById(R.id.switch1);
        isDrivingSwitch.setChecked(PrefsHelper.getSwitchValue("drivingSwitch"));

        final Switch heartAttackSwitch = findViewById(R.id.switch2);
        heartAttackSwitch.setChecked(PrefsHelper.getSwitchValue("heartAttackSwitch"));

        final Switch isDrowsySwitch = findViewById(R.id.switch3);
        isDrowsySwitch.setChecked(PrefsHelper.getSwitchValue("drowsySwitch"));
    }
}
