package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.fitbit_api_test.services.LocationPollingService;
import com.example.fitbit_api_test.utils.PrefsHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //icon 'ninja' from loading.io
        ImageView splashImage = (ImageView) findViewById(R.id.splash_image);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(splashImage);
        Glide.with(this).load(R.drawable.gears).into(imageViewTarget);
        if(new PrefsHelper(this).getLocationTrackingStatus()) {
            Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
            startService(locationSyncServiceIntent);
        }
        try {
            ScheduledFuture<?> countdown = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    Intent mapIntent = new Intent(SplashActivity.this,MainActivity.class);
                    mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mapIntent);
                }}, 5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.e(TAG, "Splash screen timer Exception:" + e.toString());
        }
    }
}
