package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fitbit_api_test.models.places;
import com.example.fitbit_api_test.utils.PrefsHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class CoffeeSuggestionsActivity extends AppCompatActivity {

    private static ArrayList<places> placeList = new ArrayList<places>();
    static View.OnClickListener optionClickListener;
    private static RecyclerView recyclerView;
    ProgressBar progressBarMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_suggestions);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        optionClickListener = new optionClickedListener(this);

        progressBarMain = new ProgressBar(CoffeeSuggestionsActivity.this);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.coffeeSuggestionLayout);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(200, 200, 200, 200);
        layout.addView(progressBarMain, params);
        progressBarMain.setVisibility(View.VISIBLE);

        try {
            getCurrentLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.placeListRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void getCurrentLocation() throws IOException {
        Log.d("getCurrentLocation", "inside get nearby coffee shops function");

        String latitude = PrefsHelper.getLatitude();
        String longitude = PrefsHelper.getLongitude();

        if(longitude!=null&&latitude!=null)
        {
            Log.d("longitude", longitude);
            Log.d("latitude", latitude);
            getData dataReadObject = new getData();
            dataReadObject.execute();
        }
        else {

            //get current location
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 99);
            }
            try {
                if (lm != null) {
                    lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                double longitude = location.getLongitude();
                                double latitude = location.getLatitude();

                                Log.d("latitude", Double.toString(latitude));
                                Log.d("longitude", Double.toString(longitude));

                                PrefsHelper.setLatitude(Double.toString(latitude));
                                PrefsHelper.setLatitude(Double.toString(longitude));
                                getData dataReadObject = new getData();
                                dataReadObject.execute();
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }, null);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    public class getData extends AsyncTask<String, String, String>{

//        ProgressBar progressBar = new ProgressBar(CoffeeSuggestionsActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.coffeeSuggestionLayout);
//
//            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.setMargins(200, 200, 200, 200);
//            layout.addView(progressBar, params);
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("getNearbyCoffeeShops", "get request in progress");
            try {
                getNearbyCoffeeShops();
                return "done";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressBarMain.isShown())
            {
                progressBarMain.setVisibility(View.INVISIBLE);
            }
            fillLayoutOptions();
        }
    }

    private void fillLayoutOptions() {

        RecyclerView.Adapter adapter = new CustomAdapter(placeList);
        recyclerView.setAdapter(adapter);
    }


    private void getNearbyCoffeeShops() throws IOException {

        //get latitude and longitude values
        String latitude = PrefsHelper.getLatitude();
        String longitude = PrefsHelper.getLongitude();

        //build url
        final String maps_key = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key="
                +maps_key+"&location="+latitude+","+longitude+"&keyword=coffee&radius=5000");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("requestUrl", urlConnection.toString());
        urlConnection.connect();
        try {
            int status = urlConnection.getResponseCode();
            if(status==200)
            {
                //parse response
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                //extract the results
                JsonElement result = jobj.get("results");
                JsonArray resultArray = result.getAsJsonArray();

                placeList.clear();

                for(int i=0;i<resultArray.size();i++)
                {
                    JsonObject placeJSONObject = resultArray.get(i).getAsJsonObject();
                    places tempPlaceObject = new places(placeJSONObject);
                    placeList.add(tempPlaceObject);
                    Log.d("JSON array value", placeJSONObject.toString());
                }
            }
            else
            {
                Log.d("getNearbyCoffeeShops", "Connection error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }


    public class optionClickedListener implements View.OnClickListener{

        private final Context context;

        private optionClickedListener(Context contextValue){

            this.context=contextValue;
        }
        @Override
        public void onClick(View v) {

            Toast.makeText(getApplicationContext(),"clicked option", Toast.LENGTH_SHORT).show();
            int selectedItem = recyclerView.getChildAdapterPosition(v);
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(selectedItem);

            places selectedOption = placeList.get(selectedItem);

            String latitude = selectedOption.getLatitude();
            String longitude = selectedOption.getLongitude();

            Log.d("clicked ", latitude);
            Log.d("clicked ", longitude);
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        }
    }

    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
