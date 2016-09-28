package com.mark.gpsalarmclock;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.mark.qpsalarmclock.R;

public class MyService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    final String LOG_TAG = "myLogs";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PendingIntent pi;
    LatLng markerLoc = null;
    int t = 5;
    NotificationCompat.Builder builder;
    Notification notification = null;
    private NotificationManagerCompat notificationManager;
    int MODE = 0;
    float radius = 100;
    Uri uri;
    boolean notifyVisible;

    private final IBinder binder = new MyServiceBinder();
    private PendingIntent pendongIntent;
    //  ArrayList<GifItem> alarmItem = new ArrayList<GifItem>();
    private String stringUri = "content://settings/system/notification_sound";
    private boolean vibroEnable;
    private boolean stopself = false;
    public boolean runalarm = false;
    private boolean signalLost = false; // если сигнал потерян
    private MyApplication myApplication;
    private Handler handler1;
    private Runnable runnableUpdateAdapter1;


    public MyService() {
    }

    public void setStopSelf(boolean stopSelf) {
        Log.d(LOG_TAG, "MyS setStopSelf" + stopSelf);
        stopself = stopSelf;
    }

    public boolean getStopSelf() {
        return stopself;
    }

    public class MyServiceBinder extends Binder {
        MyService getMyService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(LOG_TAG, "MyService onBind");
        //    throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MyS onCreate");

    }

    public void checkSignal() {

        handler1 = new Handler();

        runnableUpdateAdapter1 = new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "MyS checkSignal()");
                Log.d(LOG_TAG, "MyS hashcode = " + handler1.hashCode());

                //     Log.d(LOG_TAG, "run"+ timeout);
                if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "нет разрешений");

                    return;
                }
                if(LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient) != null) {
                    if (!LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
                        Log.d(LOG_TAG, "getLocationAvailability -false");
                        seachLocation();
                    }
                    else {
                        signalLost = false;
                    }
                }
                if(stopself) {
                    Log.d(LOG_TAG, "stopself = " +  true);
                    handler1.removeCallbacks(runnableUpdateAdapter1);

                }

                handler1.postDelayed(this, 10000);
            }
        };
        handler1.post(runnableUpdateAdapter1);
        // handler.removeCallbacks(runnableUpdateAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyS onStartCommand там " + startId);
//        handler1.removeCallbacks(runnableUpdateAdapter1);
        myApplication = (MyApplication) getApplicationContext();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                 //   .addOnConnectionFailedListener(MyService.this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d(LOG_TAG, "mGoogleApiClient !!! onConnectionFailed" +  connectionResult);
                        }
                    })
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
        mGoogleApiClient.connect();
        /*
        mGoogleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.d(LOG_TAG, "mGoogleApiClient !!! onConnectionFailed");
            }
        });
*/
        Log.d(LOG_TAG, "mGoogleApiClient.isConnected =" + mGoogleApiClient.isConnected());
        createLocationRequest();
        checkSignal();


        return super.onStartCommand(intent, flags, startId);
    }

    public void checkConnection() {
        Log.d(LOG_TAG, "Check mGoogleApiClient.isConnected =" + mGoogleApiClient.isConnected());
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            createLocationRequest();

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
      //  Log.d(LOG_TAG, "getLocationAvailability - " );
        if(LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
            Log.d(LOG_TAG, "getLocationAvailability - true" );
            signalLost=false;
        }
        else {
            Log.d(LOG_TAG, "getLocationAvailability - false" );
            seachLocation();
        }


    }

    public void seachLocation() {
        signalLost = true;
        notificationSet(0, true, signalLost);

    }


    /*
    public ArrayList<GifItem> getAlarmItem() {
        return alarmItem;
    }

    public void setAlarmItem(ArrayList ar) {
        alarmItem = ar;
    }
*/
    public void setRadius(float r) {
          Log.d(LOG_TAG, "setRadius = " + r);
        radius = r;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MyS onDestroy");
        handler1.removeCallbacks(runnableUpdateAdapter1);
        stopLocationUpdates();


        if(mGoogleApiClient!=  null) {
            mGoogleApiClient.disconnect();
        }

        if(notificationManager!=  null) notificationManager.cancel(101);


    }


    protected void createLocationRequest() {
        Log.d(LOG_TAG, " MyS createLocationRequest()");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        Log.d(LOG_TAG, "MyS startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "startLocationUpdates проблемы ");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        Log.d(LOG_TAG, "MyS stopLocationUpdates()");
         LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "MyS onLocationChanged");

        boolean notifyVisible = false;
        float  mindistance = 0;
      //  Intent intent = new Intent().putExtra(MainActivity.MY_LOCATION, location).putExtra(MainActivity.PARAM_RESULT, 1);

        for (int i = 0; i < MyApplication.alarmItem.size(); i++) {
       //    Log.d(LOG_TAG, "MyS onLocCh item = " +  alarmItem.get(i).getName());

        float[] res = new float[3];
            MyApplication.lat = (float) location.getLatitude();
            MyApplication.lng =(float) location.getLongitude();

      //  Location.distanceBetween(markerLoc.latitude, markerLoc.longitude, location.getLatitude(), location.getLongitude(), res);
        Location.distanceBetween(MyApplication.alarmItem.get(i).getlatitude(), MyApplication.alarmItem.get(i).getLongitude(), location.getLatitude(), location.getLongitude(), res);

            MyApplication.alarmItem.get(i).setDistance(res[0]);
            if(res[0]> 0 && mLocationRequest.getInterval()<4000) {
                Log.d(LOG_TAG, "Получили первое расстояние, увеличиваем время запроса");

                mLocationRequest.setInterval(5000);
            }
       // Log.d(LOG_TAG, "Расстояние в серсисе: " + res[0]);
     //   intent = new Intent().putExtra(MainActivity.PARAM_RESULT, res[0]).putExtra(MainActivity.MY_LOCATION, location);
        if (res[0] < radius && MyApplication.alarmItem.get(i).getRun()) {
/*
                    long[] pattern = { 500, 300, 400, 200 };
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(pattern, -1);

            if(vibroEnable) {
                long mills = 300L;
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(mills);
            }

            try {
              //  Uri notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Uri notify = Uri.parse(stringUri);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notify);

                Log.d(LOG_TAG, "Uri notify= " + notify);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            runAlarm(MyApplication.alarmItem.get(i), i);

                    /*
                    notification.ledARGB = Color.RED;
                    notification.ledOffMS = 0;
                    notification.ledOnMS = 1;
                    notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
                    */

        }

            if(MyApplication.alarmItem.get(i).getRun()) {
              notifyVisible = true;
                stopself = false;
                Log.d(LOG_TAG, "MyS notifyVisible = true: " + res[0]);
                if(mindistance == 0) {
                    mindistance = res[0];
                }
                else  {
                    if (mindistance > res[0])  mindistance = res[0];
                }

            }

    }


        notificationSet(mindistance, notifyVisible, signalLost);



        // останавливаем поток
        if (stopself && !notifyVisible) {
            Log.d(LOG_TAG, "делаем stopself() " );
            handler1.removeCallbacks(runnableUpdateAdapter1);
            stopSelf();
        }

    }

    public void notificationSet(float mindistance, boolean notifyVisible, boolean signalLost) {
        if(notifyVisible) {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
// оставим только самое необходимое
            if(!signalLost) {
                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("" + convertDistance(mindistance))
                        .setContentText("Расстояние до точки"); // Текст уведомления
            }
            else {
                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Ошибка")
                        .setContentText("Местоположение не определено"); // Текст уведомления
            }

        Notification notification = builder.build();

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, notification);
    }
    else {
            //   stopself = true;
            if (notificationManager != null) {
                notificationManager.cancel(101);
                runalarm = false;
            }
        }

    }

    public void runAlarm(GifItem gifItem, int i) {

        Log.d(LOG_TAG, "MyS  runAlarm() " );

        if(!runalarm) {
            runalarm = true;

            Log.d(LOG_TAG, "MyS  runAlarm() 4" );

            Intent intent2 = new Intent(MyService.this, AlarmActivity.class);
            intent2.putExtra("ID", gifItem.getId()).putExtra("pos", i);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_ONE_SHOT);
            ((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4 * 1000, pendingIntent2);
        }


    }


    public void notificationCancel() {

        runalarm = false;

        if (notificationManager!=null)
        notificationManager.cancel(101);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "MyS onConnected");
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "MyS onConnectionSuspended");
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "!!! MyS onConnectionFaile");
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