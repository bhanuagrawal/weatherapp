package com.example.weatherapp.data.entities;

import com.example.weatherapp.data.converters.WeatherDataConverter;
import com.example.weatherapp.data.datamodels.WeatherData;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.converter.PropertyConverter;
import retrofit2.Converter;

import static io.objectbox.annotation.IndexType.VALUE;

@Entity
public class LocationWeatherData {
    @Id
    public long id;

    @Unique
    @Index(type = VALUE)
    public String location;


    @Convert(converter = WeatherDataConverter.class, dbType = String.class)
    public WeatherData weatherData;

    public LocationWeatherData() {
    }

    public LocationWeatherData(String location, WeatherData weatherData) {
    }
}

