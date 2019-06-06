package com.example.weatherapp.data;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.data.datamodels.WeatherData;
import com.example.weatherapp.data.entities.LocationWeatherData;
import com.example.weatherapp.data.entities.LocationWeatherData_;
import com.example.weatherapp.network.Clients;
import com.example.weatherapp.network.WeatherService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataRepo {

    private static final String TAG = WeatherDataRepo.class.getName();
    private final FirebaseFirestore firestoreDb;
    private final CollectionReference weather;
    private Box<LocationWeatherData> locationWeatherDataBox;
    Application application;
    WeatherService weatherService;


    MutableLiveData<WeatherData> weatherData;


    public enum STATUS{
        REQUESTED, SUCCESS, FAILURE, INVALID
    }

    public WeatherDataRepo(Application application) {
        this.application = application;
        weatherService = new Clients(application).getApixuClient().create(WeatherService.class);
        locationWeatherDataBox = ObjectBox.get().boxFor(LocationWeatherData.class);
        firestoreDb = FirebaseFirestore.getInstance();
        weather = firestoreDb.collection("weather");

    }



    public void fetchLastUpdatedWeatherData() {
        List<LocationWeatherData> locationWeatherData = locationWeatherDataBox
                .query()
                .sort(new Comparator<LocationWeatherData>() {
                    @Override
                    public int compare(LocationWeatherData locationWeatherData, LocationWeatherData t1) {
                        return t1.lastUpdated.compareTo(locationWeatherData.lastUpdated);
                    }
                })
                .build()
                .find();

        if(!locationWeatherData.isEmpty()){
            getWeatherData().postValue(locationWeatherData.get(0).weatherData);
        }
    }


    public MutableLiveData<WeatherData> getWeatherData() {
        if(weatherData == null){
            weatherData = new MutableLiveData<>();
        }
        return weatherData;
    }

    public MutableLiveData<STATUS> fetchWeatherDataFromServer(String location, MutableLiveData<STATUS> weatherServiceStatus){

        weatherServiceStatus.postValue(STATUS.REQUESTED);

        weatherService.getWeatherData(location).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if(response.isSuccessful()){
                    weatherServiceStatus.postValue(STATUS.SUCCESS);
                    weatherData.postValue(response.body());
                    updateDb(location, response.body());

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

    private void updateDb(String location, WeatherData weatherData) {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                updateLocalDb(location.toLowerCase(), weatherData);
                updateFirestoreDb(location.toLowerCase(), weatherData);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }

        });

    }



    private void updateFirestoreDb(String location, WeatherData weatherData) {
        firestoreDb.collection("weather").document(location)
                .set(weatherData, SetOptions.merge());

    }


    public LocationWeatherData getLocationWeatherData(String location){
        return locationWeatherDataBox.query().equal(LocationWeatherData_.location, location.toLowerCase()).build().findUnique();
    }

    public void updateLocalDb(String location, WeatherData weatherData){

        LocationWeatherData locationWeatherData = getLocationWeatherData(location);
        if(locationWeatherData == null){
            locationWeatherData = new LocationWeatherData(new Date(), location, weatherData);
            locationWeatherDataBox.put(locationWeatherData);
        }
        else{
            locationWeatherData.weatherData = weatherData;
            locationWeatherDataBox.put(locationWeatherData);
        }


    }


}
