package com.googlemap;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.googlemap.custom.GPSTracker;
import com.googlemap.netUtils.RuntimePermissionsActivity;

public class SplashActivity extends RuntimePermissionsActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    GPSTracker gps;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.super
                        .requestAppPermissions(new
                                        String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION
                                }, R.string.runtime_permissions_txt
                                , 20);
            }
        }, 500);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

        gps = new GPSTracker(SplashActivity.this);
        if (gps.canGetLocation()) {
            if (GlobalElements.isConnectingToInternet(SplashActivity.this)) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                GlobalElements.showDialog(SplashActivity.this);
            }
        } else {
            mGoogleApiClient.connect();
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiClient,
                            builder.build()
                    );
            result.setResultCallback(this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("", "");
        gps = new GPSTracker(SplashActivity.this);
        gps.showSettingsAlert();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            try {
                gps = new GPSTracker(SplashActivity.this);
                if (gps.canGetLocation()) {
                    if (GlobalElements.isConnectingToInternet(SplashActivity.this)) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        GlobalElements.showDialog(SplashActivity.this);
                    }
                } else {
                    gps.showSettingsAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }
    }
}
