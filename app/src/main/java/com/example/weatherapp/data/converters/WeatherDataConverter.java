package com.example.weatherapp.data.converters;

import com.example.weatherapp.data.datamodels.WeatherData;
import com.google.gson.Gson;

import io.objectbox.converter.PropertyConverter;

public class WeatherDataConverter implements PropertyConverter<WeatherData, String> {

    private static Gson gson = new Gson();

    @Override
    public WeatherData convertToEntityProperty(String databaseValue) {
        return gson.fromJson(databaseValue, WeatherData.class);
    }

    @Override
    public String convertToDatabaseValue(WeatherData entityProperty) {
        return gson.toJson(entityProperty);
    }
}
