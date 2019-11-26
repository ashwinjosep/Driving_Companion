package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class coffeeSuggestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_suggestions);

        try {
            getCurrentLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.placeListRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fillLayoutOptions();
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

        @Override
        protected String doInBackground(String... strings) {
            Log.d("getNearbyCoffeeShops", "get request in progress");
//            try {
//                getNearbyCoffeeShops();
//                return "done";
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fillLayoutOptions();
        }
    }

    private void fillLayoutOptions() {

        ArrayList<places> placeList = new ArrayList<places>(10);

        char ch='a';
        for(int i=0;i<10;i++)
        {
            places place = new places(Character.toString(ch), Integer.toString(i));
            placeList.add(place);
            ch++;
        }

        RecyclerView.Adapter adapter = new CustomAdapter((ArrayList<places>) placeList);
        RecyclerView recyclerView = findViewById(R.id.placeListRecyclerView);
        recyclerView.setAdapter(adapter);
    }

    private void getNearbyCoffeeShops() throws IOException {

        String latitude = readSharedPreference("latitude");
        String longitude = readSharedPreference("longitude");
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
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                Log.d("getNearbyCoffeeShops-Buffer Values", jobj.toString());

                //need to write function to save values and test against model
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
}
