package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
                performAuthorization();
            }
        });

        //get response from login if any
        Uri data = getIntent().getData();
        if(data!=null)
        {

            Log.d("data", String.valueOf(data));
            String fragment = data.getFragment();
            if(fragment!=null)
            {
                String[] fragments = fragment.split("&")[0].split("=");

                //get access token
                String access_token = fragments[1];
                Log.d("access token", access_token);

                saveAccessToken("access_token", access_token);

            }
        }
    }

    //save to shared preferences
    public void saveAccessToken(String key, String value)
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
