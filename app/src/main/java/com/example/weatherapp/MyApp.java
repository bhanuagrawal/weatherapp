package com.example.weatherapp;

import android.app.Application;

import com.example.weatherapp.data.ObjectBox;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
    }
}
