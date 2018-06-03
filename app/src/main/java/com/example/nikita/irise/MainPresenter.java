package com.example.nikita.irise;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.nikita.irise.model.APIUtils.SunAPIUtils;
import com.example.nikita.irise.model.data.SunInfo;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.valdesekamdem.library.mdtoast.MDToast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Nikita on 01.06.2018.
 */

public class MainPresenter implements BaseContract.BasePresenter {
    private SunAPIUtils mSunAPIUtils;
    private MainActivity mMainActivity;
    private SunInfo mSunInfo;
    private CompositeDisposable mDisposables;
    private GoogleApiClient mGoogleApiClient;

    public MainPresenter(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public void onStart() {
        mDisposables = new CompositeDisposable();
        mSunAPIUtils = new SunAPIUtils();
        mGoogleApiClient = new GoogleApiClient
                .Builder(mMainActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(mMainActivity, mMainActivity)
                .build();
    }

    public void fetchDataByCoordinates(LatLng coordinates) {
        Disposable sunInfo = mSunAPIUtils.getSunInfo(coordinates.latitude, coordinates.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SunInfo>() {
                    @Override
                    public void onSuccess(SunInfo sunInfo) {
                        mSunInfo = sunInfo;
                        mMainActivity.setDayLength(sunInfo.getResults().getDayLength());
                        mMainActivity.setEnabledSwitch();
                        mMainActivity.setSunsetOnView();
                        mMainActivity.stopRotateLoading();
                        mMainActivity.switchOff();
                        mMainActivity.showMessageAboutSuccess();
                    }
                    @Override
                    public void onError(Throwable e) {
                        mMainActivity.stopRotateLoading();
                        mMainActivity.showMessageAboutError();
                    }

                });
        mDisposables.add(sunInfo);
    }

    public void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mMainActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                try {
                    fetchDataByCoordinates(likelyPlaces.get(0).getPlace().getLatLng());
                    likelyPlaces.release();
                }catch (Exception c){
                    mMainActivity.stopRotateLoading();
                    mMainActivity.showMessageAboutError();
                }
            }
        });
    }

    public String getSunriseTime(){
        return mSunInfo.getResults().getSunrise();
    }

    public String getSunsetTime(){
        return mSunInfo.getResults().getSunset();
    }

    @Override
    public void onStop() {
        mDisposables.clear();
    }
}
