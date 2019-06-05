package com.example.weatherapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.data.WeatherDataRepo;
import com.example.weatherapp.data.datamodels.WeatherData;
import com.example.weatherapp.data.entities.LocationWeatherData;

public class MainViewModel extends AndroidViewModel {

    MutableLiveData<WeatherDataRepo.STATUS> dataFetchRequestStatus;
    WeatherDataRepo weatherDataRepo;
    MutableLiveData<WeatherData> weatherData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        weatherDataRepo = new WeatherDataRepo(application);
    }

    public MutableLiveData<WeatherDataRepo.STATUS> getDataFetchRequestStatus() {
        if(dataFetchRequestStatus == null){
            dataFetchRequestStatus = new MutableLiveData<>();
        }
        return dataFetchRequestStatus;
    }

    public MutableLiveData<WeatherData> getWeatherData() {
        return weatherDataRepo.getWeatherData();
    }

    public LocationWeatherData getLocationWratherData(String location){
        fetchWeatherDataFromServer(location);
        return weatherDataRepo.getLocationWeatherData(location);

    }

    public void fetchWeatherDataFromServer(String location){
        dataFetchRequestStatus =  weatherDataRepo.fetchWeatherDataFromServer(location);
    }
}
