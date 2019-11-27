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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class coffeeSuggestionsActivity extends AppCompatActivity {

    private static ArrayList<places> placeList = new ArrayList<places>();
    static View.OnClickListener optionClickListener;
    private static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_suggestions);

        optionClickListener = new optionClickedListener(this);
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

        //get current location
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION }, 99);
        }
        try {
            if(lm!=null)
            {
                lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if(location!=null)
                        {
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();

                            Log.d("latitude", Double.toString(latitude));
                            Log.d("longitude", Double.toString(longitude));

                            saveSharedPreference("latitude", Double.toString(latitude));
                            saveSharedPreference("longitude", Double.toString(longitude));

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
            }, null);}
        } catch ( SecurityException e ) { e.printStackTrace(); }
        return;
    }

    public class getData extends AsyncTask<String, String, String>{

        ProgressBar progressBar = new ProgressBar(coffeeSuggestionsActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.coffeeSuggestionLayout);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(200, 200, 200, 200);
            layout.addView(progressBar, params);
            progressBar.setVisibility(View.VISIBLE);
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
            if(progressBar.isShown())
            {
                progressBar.setVisibility(View.INVISIBLE);
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
        String latitude = readSharedPreference("latitude");
        String longitude = readSharedPreference("longitude");

        //build url
        final String maps_key = "AIzaSyDfXc0yfnSriPI2m_eygoPTLHm_sZUaaI4";
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

    //save to shared preferences
    public void saveSharedPreference(String key, String value)
    {
        SharedPreferences sharedPref = getApplication().getSharedPreferences("mcProject", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readSharedPreference(String key)
    {
        SharedPreferences sharedPref = this.getSharedPreferences("mcProject", Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);
        return value;
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
}
