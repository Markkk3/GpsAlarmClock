package com.mark.qpsaralmclock.gpsaralmclock;

import android.*;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

public class MyService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final String LOG_TAG = "myLogs";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PendingIntent pi;
    LatLng markerLoc =  null;
    int t=5;
    Notification.Builder builder;
    Notification notification;
    private NotificationManager notificationManager;
    int MODE=0;


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(LOG_TAG, "onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        MODE = intent.getIntExtra(MainActivity.MODE_SERVICE, 0);
        Log.d(LOG_TAG, "Mode = " + MODE);
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
        mGoogleApiClient.connect();
        createLocationRequest();
        pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);

        switch (MODE) {
            case MainActivity.MODE_MY_LOCATION:
                break;
            case  MainActivity.MODE_RUN_ALARMCLOCK:

                markerLoc = intent.getParcelableExtra(MainActivity.LAT_LONG);
                Log.d(LOG_TAG, "получили маркер: " +markerLoc.latitude);

                Intent intent2 =  new Intent(this, MainActivity.class);
                TaskStackBuilder  stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent2);
                PendingIntent pendongIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

                Notification notification = new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("0 Km")
                        .setContentText("Расстояние до точки")
                        .setContentIntent(pendongIntent)
                        .build();

                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(101, notification);
/*
                builder = new Notification.Builder(getApplicationContext());
// оставим только самое необходимое
                builder.setContentIntent(pi)
                        .setSmallIcon(R.drawable.ic_setting_light)
                        .setContentTitle("0 Km")
                         .setContentText("Расстояние до точки"); // Текст уведомления

                notification = builder.build();

                notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(101, notification);
*/
                break;
        }


      //  startForeground(1, n);

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        notificationManager.cancel(101);
        Log.d(LOG_TAG, "onDestroy");
    }

    public  String getDistance() {
        return "5,5km";
    }

    protected void createLocationRequest() {
        Log.d(LOG_TAG, "createLocationRequest()");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(6000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       // startLocationUpdates();
    }

    protected void startLocationUpdates() {
        Log.d(LOG_TAG, "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    void someTask() {

        Log.d(LOG_TAG, "sometask");

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged");
        Intent intent = new Intent().putExtra(MainActivity.MY_LOCATION, location).putExtra(MainActivity.PARAM_RESULT, 1);

        switch (MODE) {
            case MainActivity.MODE_MY_LOCATION:

                break;
            case MainActivity.MODE_RUN_ALARMCLOCK:
                float[] res = new float[3];
                Location.distanceBetween(markerLoc.latitude, markerLoc.longitude, location.getLatitude(), location.getLongitude(), res);
                Log.d(LOG_TAG, "Расстояние в серсисе: " + res[0]);
                intent = new Intent().putExtra(MainActivity.PARAM_RESULT, res[0]).putExtra(MainActivity.MY_LOCATION, location);
                if (res[0]<100) {
/*
                    long[] pattern = { 500, 300, 400, 200 };
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(pattern, -1);
*/
                    long mills = 300L;
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(mills);
                    /*
                    notification.ledARGB = Color.RED;
                    notification.ledOffMS = 0;
                    notification.ledOnMS = 1;
                    notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
                    */


                }
/*
                builder.setContentTitle(convertDistance(res[0]));

                notification = builder.build();

                notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(101, notification);
*/
                break;
        }

        try {
            pi.send(MyService.this, t, intent);

        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFaile");
    }

    private String convertDistance(float distance) {
        float resultdist = 0;
        String result = "0 km";
        int r;
        if (distance < 990) {
            resultdist = Math.round(distance);
            r = (int) resultdist;
            result = ("" + r + " м");
        } else {
            if (distance < 10000) {
                r = Math.round(distance / 10);
                resultdist = (float) r / 100;
                result = ("" + resultdist + " км");
            } else {
                if (distance < 100000) {
                    r = Math.round(distance / 100);
                    resultdist = (float) r / 10;
                    result = ("" + resultdist + " км");
                } else {
                    r = Math.round(distance / 1000);
                    result = ("" + r + " км");
                }

            }
        }

        return result;

    }
}
