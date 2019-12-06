package com.example.fitbit_api_test.models;

import org.json.JSONObject;

public class RequestObject {
    private int timestampOfCurrentProcessing;
    private JSONObject locationObject;

    public int getTimestampOfCurrentProcessing() {
        return timestampOfCurrentProcessing;
    }

    public void setTimestampOfCurrentProcessing(int timestampOfCurrentProcessing) {
        this.timestampOfCurrentProcessing = timestampOfCurrentProcessing;
    }

    public JSONObject getLocationObject() {
        return locationObject;
    }

    public void setLocationObject(JSONObject locationObject) {
        this.locationObject = locationObject;
    }
}