package com.example.nikita.irise.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikita.irise.BaseContract;
import com.example.nikita.irise.MainPresenter;
import com.example.nikita.irise.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity implements BaseContract.BaseView, CompoundButton.OnCheckedChangeListener, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.constraintLayoutView)
    ConstraintLayout mLayoutView;
    @BindView(R.id.linearLayoutPlacePicker)
    ConstraintLayout mLayoutPlacePicker;
    @BindView(R.id.switchTimesOfDay)
    Switch mSwitchTimesOfDay;
    @BindView(R.id.rotateLoading)
    RotateLoading mRotateLoading;
    @BindView(R.id.textViewTimesOfDay)
    TextView mTextViewTimesOfDay;
    @BindView(R.id.textViewTime)
    TextView mTextViewTime;
    @BindView(R.id.textViewDayLengthValue)
    TextView mTextViewDayLength;
    @BindView(R.id.textViewPlace)
    TextView mTextViewPlace;
    @OnClick(R.id.imageViewLocation)
    void onClickImageViewLocation(){
        mPresenter.fetchCurrentLocation();
        startRotateLoading();
    }

    PlaceAutocompleteFragment mAutocompleteFragment;
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSwitchTimesOfDay.setOnCheckedChangeListener(this);
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        mAutocompleteFragment.setFilter(typeFilter);
        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPresenter.fetchDataByCoordinates(place.getLatLng());
                setPlace(place.getName().toString());
                startRotateLoading();
            }

            @Override
            public void onError(Status status) {
                stopRotateLoading();
                MDToast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });
        mPresenter = new MainPresenter(this);
        mPresenter.onStart();
        MDToast.makeText(getApplicationContext(), getResources().getString(R.string.startInfo), Toast.LENGTH_LONG, MDToast.TYPE_INFO).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            setSunriseOnView();
        }else{
            setSunsetOnView();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MDToast.makeText(getApplicationContext(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }

    @Override
    public void showMessageAboutSuccess() {
        MDToast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
    }

    @Override
    public void showMessageAboutError() {
        MDToast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }

    public void setSunriseOnView(){
        mLayoutView.setBackgroundResource(R.drawable.sunrise_background);
        mLayoutPlacePicker.setBackgroundColor(getResources().getColor(R.color.placePickerColorSunrise));
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.statusBarColorSunrise));
        }
        mTextViewTimesOfDay.setText(R.string.sunriseField);
        mTextViewTime.setText(mPresenter.getSunriseTime());
    }

    public void setSunsetOnView(){
        mLayoutView.setBackgroundResource(R.drawable.sunset_background);
        mLayoutPlacePicker.setBackgroundColor(getResources().getColor(R.color.placePickerColorSunset));
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.statusBarColorSunset));
        }
        mTextViewTimesOfDay.setText(R.string.sunsetField);
        mTextViewTime.setText(mPresenter.getSunsetTime());
    }

    public void setPlace(String place){
        mTextViewPlace.setText(place);
    }

    public void setDayLength(String length){
        mTextViewDayLength.setText(length);
    }

    public void setEnabledSwitch(){
        mSwitchTimesOfDay.setEnabled(true);
    }

    public void switchOff(){
        mSwitchTimesOfDay.setChecked(false);
    }

    public void startRotateLoading(){
        mRotateLoading.start();
    }

    public void stopRotateLoading(){
        mRotateLoading.stop();
    }
    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }
}
