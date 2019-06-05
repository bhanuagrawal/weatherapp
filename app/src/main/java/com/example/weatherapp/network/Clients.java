package com.example.weatherapp.network;

import android.app.Application;

import com.example.weatherapp.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Clients {


    private static final String BASE_URL = "https://api.apixu.com/" ;
    private static final String API_KEY = BuildConfig.ApixuAPIKey;

    Application application;

    private Retrofit retrofit = null;


    public Retrofit getApixuClient() {
        if (retrofit==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder defaultHttpClient= new OkHttpClient.Builder();
            defaultHttpClient.addInterceptor(logging);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            defaultHttpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();


                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("key", API_KEY)
                            .build();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);


                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(defaultHttpClient.build())
                    .build();
        }
        return retrofit;
    }

    public Clients(Application application) {
        this.application = application;
    }

}
