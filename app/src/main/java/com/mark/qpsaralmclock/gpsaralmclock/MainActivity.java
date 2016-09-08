package com.mark.qpsaralmclock.gpsaralmclock;


import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, NewPointDialog.OnCompleteListener{

    public final static int MODE_MY_LOCATION = 0;
    public final static int MODE_RUN_ALARMCLOCK= 1;
    public final static String MODE_SERVICE = "mode";
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_RESULT = "result";
    public final static String LAT_LONG = "ll";
    public final static String MY_LOCATION = "myloc";

    Handler handler;
    MapView mapView;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    final String LOG_TAG = "myLogs";
    Location myLoc;
    LatLng markerLoc =  null;
    TextView tvName;
   // TextView tvkm;
  //  ImageView imgstart;
  //  ImageView imgdelete;
    Boolean start_stop = true;
    MarkerOptions marker;
    TextView tvdebug;
    Boolean choisePoint = false;

    CardView cardview;
    int debug = 0;
    LocationRequest mLocationRequest;
    LinearLayout linLayout;
    RecyclerView rv;
    boolean zoomMap = true;
    boolean getdistanceStart = false;

    String currentNamePoint="";

    //Service myservice;


    ArrayList<GifItem> alarmItem = new ArrayList<GifItem>();
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    RvAdapter adapter;

    private  boolean bound = false;
    private MyService myService;
    int timeout = 1000;
    private Intent intent;
    private Runnable runnableUpdateAdapter;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent(this, MyService.class);

     //   getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
       // int clearCount = db.delete(DatabaseHelper.DATABASE_TABLE, null, null);
      //  Log.d(LOG_TAG, "deleted rows count = " + clearCount);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();

                new NewPointDialog().show(getFragmentManager(),
                        "login");


            }
        });

        //    createLocationRequest();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

      //  tvName = (TextView) findViewById(R.id.tvname);
      //  tvkm = (TextView) findViewById(R.id.tvkm);
      //  tvdebug = (TextView) findViewById(R.id.tvdebug);

      //  imgstart = (ImageView) findViewById(R.id.imgstart);
     //   imgdelete = (ImageView) findViewById(R.id.imgdelete);
     //   imgstart.setOnClickListener(this);

        linLayout = (LinearLayout) findViewById(R.id.lilayout);
        cardview = (CardView) findViewById(R.id.cardView);


        rv = (RecyclerView) findViewById(R.id.rv);


        adapter = new RvAdapter(alarmItem);
        rv.setAdapter(adapter);
//        rv.notify();
        //  mMap.addMarker(marker);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rv.setItemAnimator(itemAnimator);


        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

        sp =PreferenceManager.getDefaultSharedPreferences(this);

      //  Intent i = new Intent();
       // PendingIntent pi = createPendingResult(1, i, 0);


      //  bindService(new Intent(this, MyService.class).putExtra(PARAM_PINTENT, pi).putExtra(MODE_SERVICE, MODE_MY_LOCATION));
        readDatabase();

        startService(new Intent(this, MyService.class));

        if(!getdistanceStart) getDistance();
        //startService(new Intent(this, MyService.class).putExtra(PARAM_PINTENT, this);

        //  Intent intent = new Intent(this, MapsActivity.class);
        //  startActivity(intent);

    }

    public  float getRadius() {
        float radius = sp.getFloat("example_list", 100);
        return radius;
    }



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.d(LOG_TAG, "MainActivity onServiceConnected" );

          //  MyService.MyServiceBinder myServiceBinder =
          //          (MyService.MyServiceBinder) binder;
          //  myService = myServiceBinder.getMyService();
            myService = ((MyService.MyServiceBinder) binder).getMyService();

            bound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(LOG_TAG, "onServiceDisconnected");
            bound=false;
        }
    };

    private void readDatabase() {
        Log.d(LOG_TAG, "readDatabase");

        if(myService !=null) {
            alarmItem = myService.getAlarmItem();
            Log.d(LOG_TAG, "readDatabase myService !=null" + alarmItem.size());
        }

        Cursor cursor = db.query("locations", new String[] {DatabaseHelper.ID,DatabaseHelper.NAME_COLUMN, DatabaseHelper.RUN,
                        DatabaseHelper.LATITUDE_COLUMN, DatabaseHelper.LONGITUDE_COLUMN},
                null, null,
                null, null, null) ;
       // alarmItem.clear();
        int i =0 ;
        int size = alarmItem.size();


        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
            String Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_COLUMN));
            float latitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.LATITUDE_COLUMN));
            float longitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.LONGITUDE_COLUMN));
           // Log.d(LOG_TAG, " cursor.getColumnIndex = " + cursor.getColumnIndex(DatabaseHelper.LONGITUDE_COLUMN));
          //  Log.d(LOG_TAG, " cursor.getColumnIndex = " + cursor.getColumnIndex(DatabaseHelper.RUN));
           // boolean runn = cursor.get(cursor.getColumnIndex(DatabaseHelper.RUN));
            Boolean run = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RUN)) == 1);
         //   Log.d(LOG_TAG, " run = " + run);

            if (i >= size) {
                alarmItem.add(new GifItem(Name, latitude, longitude, id, run));
                Log.d(LOG_TAG, " i >= size" + i + "size = "+size);

            }
            else  {
                if ( alarmItem.get(i) == null) {
                    Log.d(LOG_TAG, " alarmItem.get(i) == null" + i);
                    alarmItem.add(new GifItem(Name, latitude, longitude, id, run));
                }
                else {
                    Log.d(LOG_TAG, "не изменяем");
                }
            }


            i++;

            /*
            LatLng latLng = new LatLng(latitude, longitude);
            marker = new MarkerOptions().position(latLng).title(Name);

            if (mMap!=null) {
                mMap.addMarker(marker);
            }
*/

            Log.d(LOG_TAG, "id=" + id +" Name =" + Name + " longitude =" + longitude + " latitude =" + latitude);
        }
        // не забываем закрывать курсор
        cursor.close();
      //  RvAdapter adapter = new RvAdapter(alarmItem);

     //   rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();


    }

    public void vibrator() {
        long mills = 500L;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);
    }

    public void runAlarm(int id, int adapterPosition) {
                Log.d(LOG_TAG, "Run Alarm" + id);
        Intent i = new Intent();
        PendingIntent pi = createPendingResult(1, i, 0);
        LatLng latlng = new LatLng(alarmItem.get(adapterPosition).getlatitude(), alarmItem.get(adapterPosition).getLongitude());

        startService(new Intent(this, MyService.class).putExtra(PARAM_PINTENT, pi).putExtra(LAT_LONG, latlng).putExtra(MODE_SERVICE, MODE_RUN_ALARMCLOCK));

    }

    public void stopAlarm(int id, int adapterPosition) {
        Log.d(LOG_TAG, "Stop Alarm" + id);
       // stopService(new Intent(this, MyService.class));

    }

    public MyService getMyService() {
        return myService;
    }
    /*
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(LOG_TAG, "onKeyDown" + event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(LOG_TAG, "KeyEvent.KEYCODE_BACK" + keyCode);
            return super.onKeyDown(KeyEvent.KEYCODE_HOME, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed ()
    {
        Log.d(LOG_TAG, " onBackPressed");
    }
*/

//удаление будильника
    public void deleteItem(int id, int i) {
     //   db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "deleteItem i =" + i);

        int delCount = db.delete(DatabaseHelper.DATABASE_TABLE, "id = " + id, null);
        Log.d(LOG_TAG, "deleted rows count = " + delCount);
        alarmItem.remove(i);
        adapter.notifyDataSetChanged();
        updateMap();

       // readDatabase();
       //alarmItem.remove();

    //    dbHelper.close();
    }
    @Override
    public void onComplete(String name) {
        Log.d(LOG_TAG, "получили =" + name);
        currentNamePoint = name;
        choisePoint = true;
       // newPoint(name);

    }
// новая точка для будильника, запись в базу
    public void newPoint(String name, double latitude, double longitude) {
        ContentValues values = new ContentValues();
              // Задайте значения для каждого столбца
        values.put(DatabaseHelper.NAME_COLUMN, name);
        values.put(DatabaseHelper.LATITUDE_COLUMN, latitude);
        values.put(DatabaseHelper.LONGITUDE_COLUMN, longitude);
        values.put(DatabaseHelper.RUN, 0);

        // Вставляем данные в таблицу
        db.insert("locations", null, values);
        alarmItem.add(new GifItem(name,  (float) latitude, (float) longitude, id, false));
        adapter.notifyDataSetChanged();

       // readDatabase();
    }

    public void saveRun(int r, int idd) {
        ContentValues values = new ContentValues();
        String g = Integer.toString(idd);
        values.put(DatabaseHelper.RUN, r);
        // обновляем по id
        int updCount = db.update(DatabaseHelper.DATABASE_TABLE, values, "id = ?",
                new String[] { g } );
        Log.d(LOG_TAG, "updated rows count = " + updCount);
    }


    public void getDistance() {
        Log.d(LOG_TAG, "getDistance");
        getdistanceStart = true;
       handler = new android.os.Handler();

       runnableUpdateAdapter = new Runnable() {
            @Override
            public void run() {
           //     Log.d(LOG_TAG, "run"+ timeout);
                if(timeout<3000) timeout +=500;
                if(myService !=null) {
                    Log.d(LOG_TAG, "getDistance, notifyDataSetChanged()");
                    //   Log.d(LOG_TAG, "myService !=null getAlarmItem = " + myService.getAlarmItem().size());
                  //  Log.d(LOG_TAG, "getDistance  = " + myService.getDistance());
                    myService.setAlarmItem(alarmItem);
                    adapter.notifyDataSetChanged();

                    if (myService.getAlarmItem() != alarmItem) {
                     //   Log.d(LOG_TAG, "myService.getAlarmItem() != alarmItem alarmItem = " + alarmItem);
                    //    Log.d(LOG_TAG, "myService.getAlarmItem() != alarmItem getAlarmItem = " + myService.getAlarmItem());
                        myService.setAlarmItem(alarmItem);

                    }

                }
                handler.postDelayed(this, timeout);
            }
        };
        handler.post(runnableUpdateAdapter);
       // handler.removeCallbacks(runnableUpdateAdapter);

    }

    public ArrayList<GifItem> getAlarmItem() {
        return alarmItem;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult requestCode = " + requestCode + ", resultCode = "
                + resultCode);

            float result = data.getFloatExtra(PARAM_RESULT, 0);
        myLoc = data.getParcelableExtra(MY_LOCATION);
     //   Log.d(LOG_TAG, "получили: " + result);
     //   Log.d(LOG_TAG, "получили местоположение: " + myLoc.getLatitude());
        if(zoomMap) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLoc.getLatitude(), myLoc.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            zoomMap=false;
        }


        for(int i=0;  i < alarmItem.size(); i++) {
            Log.d(LOG_TAG, i+ " - Name: " + alarmItem.get(i).getName() + " Run: " + alarmItem.get(i).getRun());
        }

        for(int i=0;  i < alarmItem.size(); i++) {
            float[] res = new float[3];
            Location.distanceBetween(alarmItem.get(i).getlatitude(), alarmItem.get(i).getLongitude(), myLoc.getLatitude(), myLoc.getLongitude(), res);

            //Log.d(LOG_TAG, "расстояние: " + res[0]);

            alarmItem.get(i).setDistance(res[0]);

             // tvkm.setText("" + convertDistance(res[0]));

        }
        adapter.notifyDataSetChanged();
       // rv.notify();

      //  tvkm.setText("" + convertDistance(result));
/*
        RelativeLayout.LayoutParams linLayoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        cardview.setLayoutParams(linLayoutParam);
*/

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgstart:
                if (start_stop) {
                    if(markerLoc!=null) {
                        start_stop = false;
                      //  imgstart.setImageResource(R.drawable.mr_ic_pause_light);
                        Intent i = new Intent();
                        PendingIntent pi = createPendingResult(1, i, 0);
                        startService(new Intent(this, MyService.class).putExtra(PARAM_PINTENT, pi).putExtra(LAT_LONG, markerLoc).putExtra(MODE_SERVICE, MODE_RUN_ALARMCLOCK));
                        linLayout.setBackgroundColor(Color.argb(255, 76, 175, 80));
                    }
                } else {
                    start_stop = true;
                  //  imgstart.setImageResource(R.drawable.mr_ic_play_light);
                    linLayout.setBackgroundColor(Color.argb(255, 229, 115, 115));
                    stopService(new Intent(this, MyService.class));
                }


                break;
            case R.id.imgdelete:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //  Log.d(LOG_TAG, "кликнули на карту");

                if(choisePoint) {
                    //markerLoc = latLng;
                    marker = new MarkerOptions().position(latLng).title(currentNamePoint);
                    mMap.addMarker(marker);
                    newPoint(currentNamePoint, latLng.latitude, latLng.longitude);
                    choisePoint = false;
                }

            }
        });


        updateMap();
        /*
        for(int i=0;  i < alarmItem.size(); i++) {
            alarmItem.get(i).getName();
            LatLng latLng = new LatLng(alarmItem.get(i).getlatitude(), alarmItem.get(i).getLongitude());
            marker = new MarkerOptions().position(latLng).title(alarmItem.get(i).getName());
            mMap.addMarker(marker);
        }

*/

        // Add a marker in Sydney and move the camera
        //  LatLng sydney = new LatLng(-34, 151);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void updateMap() {
        mMap.clear();
        for(int i=0;  i < alarmItem.size(); i++) {
            alarmItem.get(i).getName();
            LatLng latLng = new LatLng(alarmItem.get(i).getlatitude(), alarmItem.get(i).getLongitude());
            marker = new MarkerOptions().position(latLng).title(alarmItem.get(i).getName());
            mMap.addMarker(marker);
        }
    }

    public String convertDistance(float distance) {
        float resultdist = 0;
        String result = "0 km";
        int r;
        if (distance < 990) {
            resultdist = Math.round(distance);
            r = (int) resultdist;
            result = ("" + r + "m");
        } else {
            if (distance < 10000) {
                r = Math.round(distance / 10);
                resultdist = (float) r / 100;
                result = ("" + resultdist + "km");
            } else {
                if (distance < 100000) {
                    r = Math.round(distance / 100);
                    resultdist = (float) r / 10;
                    result = ("" + resultdist + "km");
                } else {
                    r = Math.round(distance / 1000);
                    result = ("" + r + "km");
                }

            }
        }

        return result;

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "не прошли проверку");
            // TODO: Consider calling
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(LOG_TAG, "Latitude()" + String.valueOf(mLastLocation.getLatitude()));
            Log.d(LOG_TAG, "Longitude()" + String.valueOf(mLastLocation.getLongitude()));

            myLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Minsk"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }

*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed");
    }


    public void onResume() {

        Log.d(LOG_TAG, "onResume()" );
        super.onResume();
        mapView.onResume();

            if(myService!= null) {
                String radius = sp.getString("example_list", "100");
                String uri = sp.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound");
                boolean vibro = sp.getBoolean("notifications_new_message_vibrate", true);
                int r = Integer.parseInt(radius);
                myService.setRadius(r, uri, vibro);
            }




    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
     /*   mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
*/
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, connection, 0); //Context.BIND_AUTO_CREATE
        if(!getdistanceStart) getDistance();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        handler.removeCallbacks(runnableUpdateAdapter);
        getdistanceStart=false;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
        */

        if(bound) {
            myService.setStopSelf(true);
            Log.d(LOG_TAG, "onStop bound = " + bound);
            unbindService(connection);
            bound=false;

        }
        /*
        if( myService.getStopSelf()) {
            Log.d(LOG_TAG, "делам стоп в методе активити онСтоп");
            stopService(new Intent(this, MyService.class));

        }
*/

    }



/*
    @Override
    public void onLocationChanged(Location location) {
     //   Log.d(LOG_TAG, "Слушатель местоположения)");
        debug++;
        tvdebug.setText("" + debug + " " + location.getSpeed());
        if(markerLoc!= null) {
            float[] res = new float[3];
            Location.distanceBetween(markerLoc.latitude, markerLoc.longitude, location.getLatitude(), location.getLongitude(), res);
            // Log.d(LOG_TAG, "расстояние0: " + res[0]);
          //  tvkm.setText("" + convertDistance(res[0]));
        }

    }
    */
}
