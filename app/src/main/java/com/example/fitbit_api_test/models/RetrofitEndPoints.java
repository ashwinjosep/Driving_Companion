package com.example.fitbit_api_test.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitEndPoints {

    @GET("/photos")
    Call<List<LocationObject>> getAllPhotos();

    @GET("/update")
    Call<ResponseObject> sendLocation(@Query("lat") double lat,
                                      @Query("lng") double lng,
                                      @Query("time") int time);
}