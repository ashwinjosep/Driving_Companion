package com.example.fitbit_api_test.utils;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.fitbit_api_test.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class VolleyNetworkUtils{

    public static final String TAG = VolleyNetworkUtils.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private static VolleyNetworkUtils mInstance;

    public static synchronized VolleyNetworkUtils getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}