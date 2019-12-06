package com.example.fitbit_api_test.models;

import org.json.JSONObject;

public class RequestObject {
    private int timestampOfCurrentProcessing;
    private Double lat;
    private Double lng;

    public int getTimestampOfCurrentProcessing() {
        return timestampOfCurrentProcessing;
    }

    public void setTimestampOfCurrentProcessing(int timestampOfCurrentProcessing) {
        this.timestampOfCurrentProcessing = timestampOfCurrentProcessing;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}