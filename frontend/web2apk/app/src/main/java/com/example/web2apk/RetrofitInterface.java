package com.example.web2apk;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface
{
    //in this interface we will encode the requests and parameters required for the application

    @POST("/create_app")
        Call<Void> executeCreate(@Body HashMap<String,String> map);

}
