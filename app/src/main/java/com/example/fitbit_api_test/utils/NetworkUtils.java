package com.example.fitbit_api_test.utils;

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;


public class NetworkUtils {

    public static final String TAG = NetworkUtils.class.getSimpleName();

    public static String URL= "**REMOVED**";

//    public static synchronized HttpResponse makePostRequest(String uri, String json) {
//        HttpResponse httpResponse = null;
//        try {
//            HttpPost httpPost = new HttpPost(uri);
//            httpPost.setEntity(new StringEntity(json));
//            String credentials = "**REMOVED**";
//            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//            httpPost.addHeader("Authorization", "Basic " + credBase64);
//            HttpParams httpParams = new BasicHttpParams();
//            int timeoutConnection = 10000;
//            HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
//            httpResponse = new DefaultHttpClient(httpParams).execute(httpPost);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, e.toString());
//        }
//        return httpResponse;
//    }
}
