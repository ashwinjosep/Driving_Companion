package com.example.fitbit_api_test;

public class places {

    private String name;
    private String rating;

    public places(String placeName, String placeRating)
    {
        name = placeName;
        rating = placeRating;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }
}
