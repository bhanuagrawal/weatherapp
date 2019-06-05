package com.example.weatherapp.network;

import com.example.weatherapp.data.datamodels.WeatherData;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WeatherService {
    @Headers({"Accept: application/json"})
    @GET("/v1/current.json")
    Call<WeatherData> getWeatherData(@Query("q") String location);
}