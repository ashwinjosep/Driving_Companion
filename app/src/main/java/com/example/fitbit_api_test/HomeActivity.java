package com.example.fitbit_api_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.fitbit_api_test.services.BackgroundDetectedActivitiesService;
import com.example.fitbit_api_test.services.LocationPollingService;
import com.example.fitbit_api_test.utils.PrefsHelper;
import com.google.android.gms.location.DetectedActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    // Walking: https://dribbble.com/shots/2196976-Hipster-Walk?1439641857#shot-description
    ImageView activityImage;
    TextView activityText;
    Button trackLocation;
    LinearLayout coffeeButton;
    LinearLayout callButton;
    LinearLayout settingsButton;

    GlideDrawableImageViewTarget imageViewTarget;
    BroadcastReceiver broadcastReceiver;
    PrefsHelper prefsHelper;

    int user_activity = 0;
    int confidence = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefsHelper = new PrefsHelper(this);
        initClickListenersAndViews();
        resetGif();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PrefsHelper.BROADCAST_DETECTED_ACTIVITY)) {
                    user_activity = intent.getIntExtra("type", -1);
                    confidence = intent.getIntExtra("confidence", 0);
                    resetGif();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivityTracking();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(PrefsHelper.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopActivityTracking();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void startLocationTracking() {
        prefsHelper.setStartedDriving(true);
        Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
        startService(locationSyncServiceIntent);
    }

    private void stopLocationTracking() {
        prefsHelper.setStartedDriving(false);
        Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
        stopService(locationSyncServiceIntent);
    }

    private void startActivityTracking() {
        prefsHelper.setStartedDriving(true);
        Intent intent1 = new Intent(HomeActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void stopActivityTracking() {
        prefsHelper.setStartedDriving(false);
        Intent intent = new Intent(HomeActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }

    private void resetGif() {
        if(confidence>PrefsHelper.CONFIDENCE) {
            switch (user_activity) {

                case DetectedActivity.WALKING:
                    Glide.with(this).load(R.drawable.walking).into(imageViewTarget);
                    activityText.setText("WALKING");
                    stopLocationTracking();
                    break;

                case DetectedActivity.RUNNING:
                    Glide.with(this).load(R.drawable.running).into(imageViewTarget);
                    activityText.setText("RUNNING");
                    stopLocationTracking();
                    break;

                case DetectedActivity.IN_VEHICLE:
                    Glide.with(this).load(R.drawable.driving).into(imageViewTarget);
                    activityText.setText("DRIVING");
                    startLocationTracking();
                    break;

                case DetectedActivity.ON_BICYCLE:
                    Glide.with(this).load(R.drawable.cycling).into(imageViewTarget);
                    activityText.setText("CYCLING");
                    stopLocationTracking();
                    break;

                default:
                    Glide.with(this).load(R.drawable.idle).into(imageViewTarget);
                    activityText.setText("IDLE");
                    stopLocationTracking();
                    break;
            }
        }
    }

    private void initClickListenersAndViews() {
        activityImage = (ImageView) findViewById(R.id.activity_image);
        activityText = (TextView) findViewById(R.id.activity_text);
        Button callButton = findViewById(R.id.call_button);
        Button coffeeButton = findViewById(R.id.coffee_shops_button);
        Button settingsButton = findViewById(R.id.settings_button);
        final Button trackLocation = findViewById(R.id.start_location_tracking);

        imageViewTarget = new GlideDrawableImageViewTarget(activityImage);

        trackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //toggle logic
                final String tagValue = (String) trackLocation.getTag();
                if(tagValue.equals("0"))
                {
                    trackLocation.setText(R.string.stop_tracking);
                    trackLocation.setTag("1");
                    trackLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_location_disabled_24, 0 ,0 , 0);
                }
                else
                {
                    trackLocation.setText(R.string.start_location_tracking);
                    trackLocation.setTag("0");
                    trackLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_gps_fixed_24, 0 ,0 , 0);
                }


                if(!new PrefsHelper(HomeActivity.this).getLocationTrackingStatus()) {
                    startLocationTracking();
                } else {
                    stopLocationTracking();
                }
            }
        });

        coffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent coffeeIntent = new Intent(getApplicationContext(), CoffeeSuggestionsActivity.class);
                startActivity(coffeeIntent);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(getApplicationContext(), ContactPickerActivity.class);
                startActivity(contactIntent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(HomeActivity.this,"Settings Clicked", Toast.LENGTH_SHORT).show();
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
//                user_activity = (user_activity + 1) % 5;
//                resetGif();
            }
        });
    }
}
