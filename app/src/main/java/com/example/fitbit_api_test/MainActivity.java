package com.example.fitbit_api_test;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.HttpUrl;

import static java.net.Proxy.Type.HTTP;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(savedInstanceState!=null && savedInstanceState.getString("user_id")!=null)
//        {
//            Log.d("user id ---------------", savedInstanceState.getString("user_id"));
//        }

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuthorization();
            }
        });

        Uri data = getIntent().getData();
        if(data!=null)
        {

            Log.d("data", String.valueOf(data));
            String fragment = data.getFragment();


            String[] fragments = fragment.split("&")[0].split("=");
            String access_token = fragments[1];
            Log.d("access token", access_token);

            requestHeartRate(access_token);
        }
    }

    public void requestHeartRate(String token)
    {
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("http://sample.campfirenow.com/rooms.xml");
            httpGet.setURI(uri);
            httpGet.addHeader(Bearer);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return stringBuffer.toString();
    }

    public void performAuthorization() {

        String clientId = "22BFG7";
        String clientSecret = "aaa39a78470456fdeca1f8c7abbbaa0d";
        String scopeVal = "heartrate";

        HttpUrl authorizeUrl = HttpUrl.parse("https://www.fitbit.com/oauth2/authorize") //
                .newBuilder() //
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("client_secret", clientSecret)
                .addQueryParameter("scope", scopeVal)
//                .addQueryParameter("redirect_uri", "https://www.google.com")
                .addQueryParameter("response_type", "token")
                .build();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
        startActivityForResult(i, 101);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==101)
//        {
//            Log.d("intent data", data.getStringExtra("user_id"));
//        }
//    }
}
