package com.example.nikita.irise.model.APIUtils;

import com.example.nikita.irise.ISunInfo;
import com.example.nikita.irise.model.data.SunInfo;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nikita on 02.06.2018.
 */

public class SunAPIUtils implements ISunInfo {
    public static final String BASE_URL = "https://api.sunrise-sunset.org/";

    @Override
    public Single<SunInfo> getSunInfo(double lat, double lng) {
        Retrofit retrofit = getClient(BASE_URL);
        ISunInfo weatherService = retrofit.create(ISunInfo.class);
        return weatherService.getSunInfo(lat, lng);
    }

    public static Retrofit getClient(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }
}
