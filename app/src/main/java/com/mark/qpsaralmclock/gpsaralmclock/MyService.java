package com.mark.qpsaralmclock.gpsaralmclock;

import android.*;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final String LOG_TAG = "myLogs";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PendingIntent pi;
    LatLng markerLoc =  null;
    int t=5;
    NotificationCompat.Builder builder;
    Notification notification = null;
    private NotificationManagerCompat notificationManager;
    int MODE=0;
    float radius =100;
    Uri uri;

    private final IBinder binder = new MyServiceBinder();
    private PendingIntent pendongIntent;
    ArrayList<GifItem> alarmItem = new ArrayList<GifItem>();
    private String stringUri = "content://settings/system/notification_sound";
    private boolean vibroEnable;
    private boolean stopself = false;
    public  boolean runalarm = false;


    public MyService() {
    }

    public void setStopSelf(boolean stopSelf) {
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
/*
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
*/
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyS onStartCommand" + startId);
       // MODE = intent.getIntExtra(MainActivity.MODE_SERVICE, 0);
      //  Log.d(LOG_TAG, "Mode = " + MODE);
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


       // pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);



           //     markerLoc = intent.getParcelableExtra(MainActivity.LAT_LONG);
//                Log.d(LOG_TAG, "MyS получили маркер: " +markerLoc.latitude);
/*
                Intent intent2 =  new Intent(this, MainActivity.class);
                TaskStackBuilder  stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent2);
               pendongIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

                notification = new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("0 Km")
                        .setContentText("Расстояние до точки")
                        .setContentIntent(pendongIntent)
                        .build();

                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(101, notification);
                */
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


      //  startForeground(1, n);

        return super.onStartCommand(intent, flags, startId);
    }

    public ArrayList<GifItem> getAlarmItem() {

       // Log.d(LOG_TAG, "MyS  getAlarmItem = " + alarmItem.size());

        return alarmItem;
    }

    public void setAlarmItem(ArrayList ar) {
      //  Log.d(LOG_TAG, "MyS setAlarmItem = " + ar.size());

        alarmItem = ar;
    }

    public void setRadius(float r, String uri, boolean vibro) {
          Log.d(LOG_TAG, "setRadius = " + r);
        stringUri = uri;
        radius = r;
        vibroEnable = vibro;
    }



    public void onDestroy() {
        super.onDestroy();
        /*
        if(mGoogleApiClient!=  null) {
            mGoogleApiClient.disconnect();
        }
*/
       // if(notificationManager!=  null) notificationManager.cancel(101);

        Log.d(LOG_TAG, "MyS onDestroy");
    }

    public  String getDistance() {
        return "5,5km";
    }

    protected void createLocationRequest() {
        Log.d(LOG_TAG, " MyS createLocationRequest()");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

       // startLocationUpdates();
    }

    protected void startLocationUpdates() {
        Log.d(LOG_TAG, "MyS startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "MyS onLocationChanged");

        boolean notifyVisible = false;
        float  mindistance = 0;
      //  Intent intent = new Intent().putExtra(MainActivity.MY_LOCATION, location).putExtra(MainActivity.PARAM_RESULT, 1);

        for (int i = 0; i < alarmItem.size(); i++) {
       //    Log.d(LOG_TAG, "MyS onLocCh item = " +  alarmItem.get(i).getName());

        float[] res = new float[3];
      //  Location.distanceBetween(markerLoc.latitude, markerLoc.longitude, location.getLatitude(), location.getLongitude(), res);
        Location.distanceBetween(alarmItem.get(i).getlatitude(), alarmItem.get(i).getLongitude(), location.getLatitude(), location.getLongitude(), res);

            alarmItem.get(i).setDistance(res[0]);
       // Log.d(LOG_TAG, "Расстояние в серсисе: " + res[0]);
     //   intent = new Intent().putExtra(MainActivity.PARAM_RESULT, res[0]).putExtra(MainActivity.MY_LOCATION, location);
        if (res[0] < radius && alarmItem.get(i).getRun()) {
/*
                    long[] pattern = { 500, 300, 400, 200 };
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(pattern, -1);


*/
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
            runAlarm();

                    /*
                    notification.ledARGB = Color.RED;
                    notification.ledOffMS = 0;
                    notification.ledOnMS = 1;
                    notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
                    */

        }

            if(alarmItem.get(i).getRun()) {
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

        if(notifyVisible) {
            /*
            Intent intent  = new Intent(this, MainActivity.class);
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(""+ convertDistance(mindistance))
                    .setContentText("Расстояние до точки")
                    .setContentIntent(pendongIntent)
                    .build();
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(101, notification);
*/
            Intent notificationIntent = new Intent(this, MainActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
// оставим только самое необходимое
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(""+ convertDistance(mindistance))
                    .setContentText("Расстояние до точки"); // Текст уведомления

            Notification notification = builder.build();

            notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(101, notification);
        }
        else {
            stopself = true;
             if (notificationManager!=null)
              notificationManager.cancel(101);
        }
/*

        // останавливаем поток
        if (stopself && !notifyVisible) {
            Log.d(LOG_TAG, "делаем stopself() " );
            stopService(new Intent(this, MyService.class));
        }
        */
/*
                   

                notification = builder.build();

              //  notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(101, notification);
*/
/*
        try {
            pi.send(MyService.this, t, intent);

        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
*/

    }

    public void runAlarm() {

        Log.d(LOG_TAG, "MyS  runAlarm() " );
     //   AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
  //      Intent intent=new Intent(MyService.this, MainActivity.class);
    //    intent.putExtra("ONE_TIME", Boolean.FALSE);//Задаем параметр интента
    //    PendingIntent pi= PendingIntent.getBroadcast(this,0, intent,0);
//Устанавливаем интервал срабатывания в 5 секунд.
    //    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4000, pi);
        /*
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Log.d(LOG_TAG, "MyS  runAlarm() 1" );
        PendingIntent pintent = PendingIntent.getService(MyService.this, 0, intent, 0);
        Log.d(LOG_TAG, "MyS  runAlarm() 2" );
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Log.d(LOG_TAG, "MyS  runAlarm() 3" );
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5*1000, pintent);
        Log.d(LOG_TAG, "MyS  runAlarm() 4" );
       // am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000*5,pi);
       */
        /*
        if(!runalarm) {
            runalarm = true;
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, AlarmManagerBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT );
// На случай, если мы ранее запускали активити, а потом поменяли время,
// откажемся от уведомления
            // am.cancel(pendingIntent);
// Устанавливаем разовое напоминание
            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+4000, pendingIntent);
            Log.d(LOG_TAG, "MyS  runAlarm() 4" );
        }
        */

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
        Log.d(LOG_TAG, "MyS onConnectionFaile");
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