package com.example.nikita.irise;

import com.example.nikita.irise.model.data.SunInfo;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Nikita on 01.06.2018.
 */

public interface ISunInfo {
    @GET("json?date=today")
    Single<SunInfo> getSunInfo(@Query("lat") double lat, @Query("lng") double lng);
}
