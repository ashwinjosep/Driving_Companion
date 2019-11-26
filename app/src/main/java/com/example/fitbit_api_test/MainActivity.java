package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                saveSharedPreference("access_token", access_token);
                //move to phone picker activity
                Intent phoneIntent = new Intent(this, coffeeSuggestionsActivity.class);
                startActivity(phoneIntent);
            }
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

    public String readSharedPreference(String key)
    {
        SharedPreferences sharedPref = this.getSharedPreferences("mcProject", Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);
        return value;
    }

    public void getHeartRate() throws IOException {
        Log.d("getHeartRate", "inside get heart rate function");

        URL url = new URL("https://api.fitbit.com/1/user/-/activities/heart/date/today/1d.json");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String access_token = readSharedPreference("access_token");
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

    //save to shared preferences
    public void saveSharedPreference(String key, String value)
    {
        SharedPreferences sharedPref = getApplication().getSharedPreferences("mcProject", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
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
