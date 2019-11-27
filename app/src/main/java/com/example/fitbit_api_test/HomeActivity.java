package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.fitbit_api_test.services.LocationPollingService;
import com.example.fitbit_api_test.utils.PrefsHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();


    // Walking: https://dribbble.com/shots/2196976-Hipster-Walk?1439641857#shot-description
    //
    ImageView activityImage;
    TextView activityText;
    Button trackLocation;
    LinearLayout coffeeButton;
    LinearLayout callButton;
    LinearLayout settingsButton;

    GlideDrawableImageViewTarget imageViewTarget;

    int user_activity = 0;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initClickListenersAndViews();
        resetGif();
//        try {
//            final ScheduledFuture<?> countdown = scheduler.schedule(new Runnable() {
//                @Override
//                public void run() {
//                    user_activity = (user_activity + 1) % 5;
//                    resetGif();
//                }
//            }, 3000, TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            Log.e(TAG, "onMapReady Exception:" + e.toString());
//        }
    }

    private void resetGif() {
        switch (user_activity) {
            case 0:
                Glide.with(this).load(R.drawable.idle1).into(imageViewTarget);
                activityText.setText("IDLE");
                break;

            case 1:
                Glide.with(this).load(R.drawable.walking).into(imageViewTarget);
                activityText.setText("WALKING");
                break;

            case 2:
                Glide.with(this).load(R.drawable.running).into(imageViewTarget);
                activityText.setText("RUNNING");
                break;

            case 3:
                Glide.with(this).load(R.drawable.driving).into(imageViewTarget);
                activityText.setText("DRIVING");
                break;

            case 4:
                Glide.with(this).load(R.drawable.cycling).into(imageViewTarget);
                activityText.setText("CYCLING");
                break;
        }
    }

    private void initClickListenersAndViews() {
        activityImage = (ImageView) findViewById(R.id.activity_image);
        activityText = (TextView) findViewById(R.id.activity_text);
        callButton = (LinearLayout) findViewById(R.id.call_button);
        coffeeButton = (LinearLayout) findViewById(R.id.coffee_shops_button);
        settingsButton = (LinearLayout) findViewById(R.id.settings_button);
        trackLocation = (Button) findViewById(R.id.start_location_tracking);

        imageViewTarget = new GlideDrawableImageViewTarget(activityImage);

        trackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!new PrefsHelper(HomeActivity.this).getLocationTrackingStatus()) {
                    Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                    startService(locationSyncServiceIntent);
                } else {
                    Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                    stopService(locationSyncServiceIntent);
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
                user_activity = (user_activity + 1) % 5;
                resetGif();
            }
        });
    }
}
