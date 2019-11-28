package com.example.fitbit_api_test.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    //Preference Variables
    public static final String LOCATION_TRACK_STATUS = "location_track_status";
    public static final String UPDATING_LOCATION_STATUS = "updating_location";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EMERGENCY_NAME = "name";
    public static final String EMERGENCY_NUMBER = "number";
    public static final String STARTED_DRIVING = "started_driving";
    public static final String PREF_NAME = "LocationPreferenceFile";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    //Constants
    public static final String GPS_ENABLED = "gps_enabled";
    public static final String GPS_NOT_ENABLED = "gps_not_enabled";
    public static final String PLUGGED_USB = "PLUGGED_USB";
    public static final String PLUGGED_AC = "PLUGGED_AC";
    public static final String PLUGGED_WIRELESS = "PLUGGED_WIRELESS";
    public static final String CHARGING = "CHARGING";
    public static final String NONE = "NONE";
    public static final String UNAVAILABLE = "UNAVAILABLE";

    //Parameters for Activity Recognition
    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000;
    public static final int CONFIDENCE = 70;

    //Location Polling variables
    public static final int TIME_THRESHOLD = 30000;
    public static final int TIME_THRESHOLD_FOR_PRECISION = 20000;
    public static final int LOCATION_UPDATE_TIME = 5000;
    public static final float LOCATION_THRESHOLD = 100;

    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    public PrefsHelper(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public Boolean getLocationTrackingStatus(){
        return pref.getBoolean(LOCATION_TRACK_STATUS, false);
    }

    public void setLocationTrackingStatus(Boolean status){
        editor.putBoolean(LOCATION_TRACK_STATUS, status);
        editor.apply();
    }

    public Boolean getUpdatingLocationStatus(){
        return pref.getBoolean(UPDATING_LOCATION_STATUS, false);
    }

    public void setUpdatingLocationStatus(Boolean status){
        editor.putBoolean(UPDATING_LOCATION_STATUS, status);
        editor.apply();
    }

    public String getAccessToken(){
        return pref.getString(ACCESS_TOKEN,null);
    }

    public void setAccessToken(String status){
        editor.putString(ACCESS_TOKEN,"");
        editor.apply();
    }

    public String getEmergencyContactName(){
        return pref.getString(EMERGENCY_NAME,null);
    }

    public void setEmergencyContactName(String name){
        editor.putString(EMERGENCY_NAME,name);
        editor.apply();
    }

    public String getEmergencyContactNumber(){
        return pref.getString(EMERGENCY_NUMBER,null);
    }

    public void setEmergencyContactNumber(String number){
        editor.putString(EMERGENCY_NUMBER,number);
        editor.apply();
    }

    public Boolean getStartedDriving(){
        return pref.getBoolean(STARTED_DRIVING, false);
    }

    public void setStartedDriving(Boolean status){
        editor.putBoolean(STARTED_DRIVING, status);
        editor.apply();
    }

    public static String getLatitude()
    {
        return pref.getString(LATITUDE, null);
    }

    public static String getLongitude()
    {
        return pref.getString(LONGITUDE, null);
    }

    public static void setLatitude(String latitude)
    {
        editor.putString(LATITUDE, latitude);
        editor.apply();
    }
    public static void setLongitude(String longitude)
    {
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }
    public static void setSwitchValue(String key, Boolean value)
    {
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static boolean getSwitchValue(String key)
    {
        return pref.getBoolean(key, false);
    }
}
