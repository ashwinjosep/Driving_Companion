package com.example.fitbit_api_test.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    //Preference Variables
    public static final String LOCATION_TRACK_STATUS = "location_track_status";
    public static final String UPDATING_LOCATION_STATUS = "updating_location";
    public static final String PREF_NAME = "LocationPreferenceFile";

    //Constants
    public static final String GPS_ENABLED = "gps_enabled";
    public static final String GPS_NOT_ENABLED = "gps_not_enabled";
    public static final String PLUGGED_USB = "PLUGGED_USB";
    public static final String PLUGGED_AC = "PLUGGED_AC";
    public static final String PLUGGED_WIRELESS = "PLUGGED_WIRELESS";
    public static final String CHARGING = "CHARGING";
    public static final String NONE = "NONE";
    public static final String UNAVAILABLE = "UNAVAILABLE";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
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
}
