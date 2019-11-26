package com.example.fitbit_api_test.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitEndPoints {

    @GET("/photos")
    Call<List<LocationObject>> getAllPhotos();

    @POST("/posts")
    @FormUrlEncoded
    Call<ResponseObject> sendLocation(@Body RequestObject locationObject);
}