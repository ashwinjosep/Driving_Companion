package com.example.fitbit_api_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.fitbit_api_test.services.LocationPollingService;
import com.example.fitbit_api_test.utils.PrefsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1423;
    private static final String TAG = MainActivity.class.getSimpleName();
    private PrefsHelper prefsHelper;
    private AlertDialog dialog;

    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_RESOLVE_ERROR = 2;
    private static final String DIALOG_ERROR = "dialog_error_play_services";
    private static final String DIALOG_TAG = "dialog_tag_error_play_services";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private ErrorDialogFragment mDialogFragment;
    private LocationRequest mLocationRequestBalancedPowerAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsHelper = new PrefsHelper(this);

        //Settings Button Logic
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });



        ImageView loginPageImage = (ImageView) findViewById(R.id.login_page_gif);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(loginPageImage);
        Glide.with(this).load(R.drawable.road_trip).into(imageViewTarget);

        //set on click for login button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked on login button", Toast.LENGTH_SHORT).show();
                performAuthorization();
                //read intra day heart rate
                getData fitbitObject = new getData();
                fitbitObject.execute();
            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder details_builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater details_inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View select_place_prompt = details_inflater.inflate(R.layout.alert_dialog_location, null);
                    details_builder.setView(select_place_prompt);
                    Button ok = (Button) select_place_prompt.findViewById(R.id.ok_button);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            cancelDialog();
                        }
                    });
                    dialog = details_builder.show();
                }else{
                    if(!prefsHelper.getLocationTrackingStatus()){
                        prefsHelper.setLocationTrackingStatus(true);
                        Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                        startService(locationSyncServiceIntent);
                    }else{
                        prefsHelper.setLocationTrackingStatus(false);
                    }
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            //get response from login if any
            Uri data = getIntent().getData();
            if(data!=null)
            {
                String fragment = data.getFragment();
                if(fragment!=null)
                {
                    String[] fragments = fragment.split("&")[0].split("=");

                    //get access token
                    String access_token = fragments[1];
                    prefsHelper.setAccessToken(access_token);
                    //move to phone picker activity
                    if(prefsHelper.getEmergencyContactName()==null || prefsHelper.getEmergencyContactNumber() == null) {
                        Intent homeIntent = new Intent(this, HomeActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    } else {
                        Intent contactsIntent = new Intent(this, ContactPickerActivity.class);
                        contactsIntent.putExtra("initial_contact",true);
                        startActivity(contactsIntent);
                    }
                }
            }
        }
    }

    private void cancelDialog() {
        dialog.dismiss();
        dialog.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    if(mGoogleApiClient==null) {
                        enableLocationServices();
                        if (!prefsHelper.getLocationTrackingStatus()) {
                            prefsHelper.setLocationTrackingStatus(true);
                            Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                            startService(locationSyncServiceIntent);
                        } else {
                            prefsHelper.setLocationTrackingStatus(false);
                        }
                    }
                    if(prefsHelper.getLocationTrackingStatus()) {
                        Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                        startService(locationSyncServiceIntent);
                    }
                }else{
                    AlertDialog.Builder details_builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater details_inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View select_place_prompt = details_inflater.inflate(R.layout.alert_dialog_location, null);
                    details_builder.setView(select_place_prompt);
                    Button ok = (Button) select_place_prompt.findViewById(R.id.ok_button);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            cancelDialog();
                        }
                    });
                    dialog = details_builder.show();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (prefsHelper.getLocationTrackingStatus()) {
                            Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                            startService(locationSyncServiceIntent);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "Enable location services for seamless functioning", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Location services unavailable on this device, functionality may be affected", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case REQUEST_RESOLVE_ERROR:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mResolvingError = false;
                        if (mDialogFragment != null) {
                            mDialogFragment.dismiss();
                            mDialogFragment = null;
                        }
                        if (!mGoogleApiClient.isConnecting() &&
                                !mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                            Log.e(TAG, "Result is true after resolving error");
                            if (prefsHelper.getLocationTrackingStatus()) {
                                Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                                startService(locationSyncServiceIntent);
                            }
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        if (mDialogFragment != null) {
                            mDialogFragment.dismiss();
                            mDialogFragment = null;
                        }
                        Toast.makeText(getApplicationContext(), "Please install google play services and try again", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        if (mDialogFragment != null) {
                            mDialogFragment.dismiss();
                            mDialogFragment = null;
                        }
                        Toast.makeText(getApplicationContext(), "Unsupported device", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void enableLocationServices() {
        buildGoogleApiClient();
        setUpLocationRequests();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    private void setUpLocationRequests() {
        mLocationRequestBalancedPowerAccuracy = new LocationRequest();
        mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestBalancedPowerAccuracy.setInterval(3000);
        mLocationRequestBalancedPowerAccuracy.setFastestInterval(1000);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = true;
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    private void showErrorDialog(int errorCode) {
        if (mDialogFragment == null) {
            mDialogFragment = new ErrorDialogFragment();
            Bundle args = new Bundle();
            args.putInt(DIALOG_ERROR, errorCode);
            mDialogFragment.setArguments(args);
            mDialogFragment.setCancelable(false);
            mDialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if(prefsHelper.getLocationTrackingStatus()) {
                            Intent locationSyncServiceIntent = new Intent(getApplicationContext(), LocationPollingService.class);
                            startService(locationSyncServiceIntent);
                        }

                        return;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(getApplicationContext(), "Location services not available. Please enable them for proceeding further.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {

        public ErrorDialogFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }
    }

    public class getData extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("getHeartRate", "get request completed");
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("getHeartRate", "get request in progress");
            try {
                getHeartRate();
                return "done";
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

    public void getHeartRate() throws IOException {
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

                //need to write function to save values and test against model
            }
            else
            {
                Log.d("getHeartrate", "Connection error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    public void performAuthorization() {

        String clientId = "22BFG7";
        String clientSecret = "aaa39a78470456fdeca1f8c7abbbaa0d";
        String scopeVal = "heartrate sleep";

        HttpUrl authorizeUrl = HttpUrl.parse("https://www.fitbit.com/oauth2/authorize") //
                .newBuilder() //
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("client_secret", clientSecret)
                .addQueryParameter("scope", scopeVal)
                .addQueryParameter("response_type", "token")
                .build();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
        startActivityForResult(i, 101);

    }
}
