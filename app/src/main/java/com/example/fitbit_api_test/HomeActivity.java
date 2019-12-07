package com.example.fitbit_api_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.fitbit_api_test.services.BackgroundDetectedActivitiesService;
import com.example.fitbit_api_test.services.LocationPollingService;
import com.example.fitbit_api_test.utils.PrefsHelper;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

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
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

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
        if(new PrefsHelper(this).getLocationTrackingStatus()) {
            Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
            startService(locationSyncServiceIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopActivityTracking();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void startLocationTracking() {
        Log.d("startLocationTracking", "inside start location tracking function");
        //isDrowsyOverride
        if (PrefsHelper.getSwitchValue("drowsySwitch")&&PrefsHelper.getSwitchValue("drivingSwitch"))
        {
            Log.d("displayDrowsy", "conditions met");
            SimulateDrowsiness object = new SimulateDrowsiness();
            object.execute();
        }
        startTimer();
        prefsHelper.setStartedDriving(true);
        prefsHelper.setLocationTrackingStatus(true);
        Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
        startService(locationSyncServiceIntent);
    }

    private void stopLocationTracking() {
        prefsHelper.setStartedDriving(false);
        prefsHelper.setLocationTrackingStatus(false);
        Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
        stopService(locationSyncServiceIntent);
        stoptimertask();
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

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask, 1000, 15000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        GetData fitbitObject = new GetData();
                        fitbitObject.execute();
                    }
                });
            }
        };
    }

    private void resetGif() {

        //settings override for driving option
        if(PrefsHelper.getSwitchValue("drivingSwitch"))
        {
            Glide.with(HomeActivity.this).load(R.drawable.driving).into(imageViewTarget);
            activityText.setText("DRIVING");
        }
        else {

            if (confidence > PrefsHelper.CONFIDENCE) {
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
                    Log.d("startLocationTracking", "inside start location tracking function");
                    //isDrowsyOverride
                    if (PrefsHelper.getSwitchValue("drowsySwitch")&&PrefsHelper.getSwitchValue("drivingSwitch"))
                    {
                        Log.d("displayDrowsy", "conditions met");
                        SimulateDrowsiness object = new SimulateDrowsiness();
                        object.execute();

                    }
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
                }
                else {
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

    public class SimulateDrowsiness extends AsyncTask<String, String, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            displayDrowsinessAlert();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void displayDrowsinessAlert() {
            Log.d("displayDrowsy", "inside dialog builder for drowsiness");
            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.alert_dialog_drowsy, null));


            final AlertDialog dialog = builder.create();
            dialog.show();
            playBeep();
            vibrateForTime(1000);

            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrefsHelper.setSwitchValue("drowsySwitch", false);
                    dialog.dismiss();
                }
            });
        }
    }
    //function to vibrate for specific time in milliseconds
    public void vibrateForTime(int duration)
    {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(duration);
        }
    }

    //function to play beep sound
    public void playBeep()
    {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play( );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject getHeartRate() throws IOException {
        Log.d("getHeartRate", "inside get heart rate function");

        URL url = new URL("https://api.fitbit.com/1/user/-/activities/heart/date/today/1d.json");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String access_token = prefsHelper.getAccessToken();
        String bearerAuth = "Bearer "+access_token;
        urlConnection.setRequestProperty("Authorization", bearerAuth);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        try {
            int status = urlConnection.getResponseCode();
            if(status==200)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                Log.d("getHeartRate-Buffer Values", jobj.toString());
                return jobj;

                //need to write function to save values and test against model
            }
            else
            {
                Log.d("getHeartrate", "Connection error");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    public class GetData extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("getHeartRate", "get request completed");
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonObject heartRate;
            Log.d("getHeartRate", "get request in progress");
            try {
                heartRate = getHeartRate();
                if(heartRate!=null){
                    // send Heart Rate
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("getHeartRate", "get request completed");
        }
    }

}
