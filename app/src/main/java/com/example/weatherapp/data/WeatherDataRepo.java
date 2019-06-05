package com.example.weatherapp.data;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.data.datamodels.WeatherData;
import com.example.weatherapp.data.entities.LocationWeatherData;
import com.example.weatherapp.data.entities.LocationWeatherData_;
import com.example.weatherapp.network.Clients;
import com.example.weatherapp.network.WeatherService;

import io.objectbox.Box;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataRepo {

    private Box<LocationWeatherData> locationWeatherDataBox;
    Application application;
    WeatherService weatherService;


    MutableLiveData<WeatherData> weatherData;

    public enum STATUS{
        NOT_REQUESTED, REQUESTED, SUCCESS, FAILURE, INVALID
    }

    public WeatherDataRepo(Application application) {
        this.application = application;
        weatherService = new Clients(application).getApixuClient().create(WeatherService.class);
        locationWeatherDataBox = ObjectBox.get().boxFor(LocationWeatherData.class);
    }


    public MutableLiveData<WeatherData> getWeatherData() {
        if(weatherData == null){
            weatherData = new MutableLiveData<>();
        }
        return weatherData;
    }

    public MutableLiveData<STATUS> fetchWeatherDataFromServer(String location){


        MutableLiveData<STATUS> weatherServiceStatus = new MutableLiveData<>();
        weatherServiceStatus.postValue(STATUS.REQUESTED);

        weatherService.getWeatherData(location).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if(response.isSuccessful()){
                    weatherServiceStatus.postValue(STATUS.SUCCESS);
                    weatherData.postValue(response.body());
                    addLocationWeatherData(location.toLowerCase(), response.body());
                }
                else if (response.code() == 400) {
                    weatherServiceStatus.postValue(STATUS.INVALID);
                }
                else{
                    weatherServiceStatus.postValue(STATUS.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                weatherServiceStatus.postValue(STATUS.SUCCESS);

            }
        });

        return weatherServiceStatus;
    }


    public LocationWeatherData getLocationWeatherData(String location){
        return locationWeatherDataBox.query().equal(LocationWeatherData_.location, location.toLowerCase()).build().findUnique();
    }

    public void addLocationWeatherData(String location, WeatherData weatherData){
        LocationWeatherData locationWeatherData = new LocationWeatherData(location, weatherData);
        locationWeatherDataBox.put(locationWeatherData);
        
    }

    public void updateLocationWeatherData(String location, WeatherData weatherData){
        LocationWeatherData locationWeatherData = new LocationWeatherData(location, weatherData);
        locationWeatherDataBox.remove(locationWeatherData);
        locationWeatherDataBox.put(locationWeatherData);
    }

    public void deleteLocationWeatherData(String location, WeatherData weatherData){
        LocationWeatherData locationWeatherData = new LocationWeatherData(location, weatherData);
        locationWeatherDataBox.remove(locationWeatherData);
    }


}
