package com.mark.gpsalarmclock;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.mark.qpsalarmclock.R;

import java.util.ArrayList;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.Thing;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NewPointDialog.OnCompleteListener{

    public final static int MODE_MY_LOCATION = 0;
    public final static int MODE_RUN_ALARMCLOCK= 1;
    public final static String MODE_SERVICE = "mode";
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_RESULT = "result";
    public final static String LAT_LONG = "ll";
    public final static String MY_LOCATION = "myloc";

    public final static String MY_LOCATION_LAT = "mylocLAT";
    public final static String MY_LOCATION_LNG = "mylocLNG";

    Handler handler;
    MapView mapView;
    private GoogleMap mMap;
 //   GoogleApiClient mGoogleApiClient;
    final String LOG_TAG = "myLogs";
    LatLng myLoc;
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

    boolean openSetting = false;

    String currentNamePoint="";

    //Service myservice;


 //   public ArrayList<GifItem> alarmItem = new ArrayList<GifItem>();
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    RvAdapter adapter;

    private  boolean bound = false;
    private MyService myService;
    int timeout = 1000;
    private Intent intent;
    private Runnable runnableUpdateAdapter;
    private SharedPreferences sp;
    private SharedPreferences sPref;


    MyApplication myApplication;

    //Circle circle;
    //Polyline polyline;
    ArrayList<Polyline> polylineArrayList = new ArrayList<Polyline>();
    ArrayList<Circle> circleArrayList = new ArrayList<Circle>();

    private  TextView tvchoisePoint;
    private ViewGroup.LayoutParams params;
    private LinearLayout.LayoutParams paramsLinear;
    private RelativeLayout.LayoutParams lParams;
    private FrameLayout.LayoutParams lParams2;
    private LinearLayout linLayoutConteiner;
    int heightMap;
    int heightScreen;
    Button btnadd;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);

        myApplication =(MyApplication) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        readSharePreferences();
        intent = new Intent(this, MyService.class);

        dbHelper = new DatabaseHelper(this);

       // int clearCount = db.delete(DatabaseHelper.DATABASE_TABLE, null, null);
      //  Log.d(LOG_TAG, "deleted rows count = " + clearCount);

      //  FAB_Float_on_Scroll fab = (FAB_Float_on_Scroll) findViewById(R.id.fab) ;

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
   //     fab.setVisibility(View.GONE);
        btnadd = (Button) findViewById(R.id.btnadd);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewPointDialog().show(getFragmentManager(),
                        "login");
            }
        });

        //    createLocationRequest();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        linLayoutConteiner = (LinearLayout) findViewById(R.id.linlayoutConteiner);

        linLayout = (LinearLayout) findViewById(R.id.lilayout);
        cardview = (CardView) findViewById(R.id.cardView);

        tvchoisePoint = (TextView) findViewById(R.id.tvchoisepoint);

        rv = (RecyclerView) findViewById(R.id.rv);

        adapter = new RvAdapter(MyApplication.alarmItem);
        rv.setAdapter(adapter);
//        rv.notify();
        //  mMap.addMarker(marker);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rv.setItemAnimator(itemAnimator);
/*        rv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                 Log.d(LOG_TAG, "child " + i);
                 Log.d(LOG_TAG, "AonNestedScroll1 " + i1);
                 Log.d(LOG_TAG, "AonNestedScroll2 " + i2);
                 Log.d(LOG_TAG, "AonNestedScroll3" + i3);
                //  Log.d(LOG_TAG, "dxConsume " + dxConsumed + " y= " + dyConsumed);
                //   Log.d(LOG_TAG, "dxUnconsumed " + dxUnconsumed + "y " + dyUnconsumed);
                //child -> Floating Action Button
            }
        });
*/
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
*/
        sp =PreferenceManager.getDefaultSharedPreferences(this);

      //  Intent i = new Intent();
       // PendingIntent pi = createPendingResult(1, i, 0);

      //  bindService(new Intent(this, MyService.class).putExtra(PARAM_PINTENT, pi).putExtra(MODE_SERVICE, MODE_MY_LOCATION));
        readDatabase();
/*
        Intent intent2 = new Intent(this, AlarmActivity.class);
        intent2.putExtra("ID", MyApplication.alarmItem.get(1).getId()).putExtra("pos", 1).putExtra("NAME",  MyApplication.alarmItem.get(1).getName());
        startActivity(intent2);*/
        startService(new Intent(this, MyService.class));

        if(!getdistanceStart) getDistance();
        //startService(new Intent(this, MyService.class).putExtra(PARAM_PINTENT, this);

        //  Intent intent = new Intent(this, MapsActivity.class);
        //  startActivity(intent);
      //  sceneRoot = (ViewGroup) findViewById(R.id.framelayout);
       // cardview1 =  sceneRoot.findViewById(R.id.cardView);
      //  params = cardview.getLayoutParams();

      //  lParams2 = (FrameLayout.LayoutParams) sceneRoot.getLayoutParams();
      //  heightMap = params.height;
      //  heightScreen = lParams2.height;
     //   Log.d(LOG_TAG, "высота карты =" + heightMap);
      //  Log.d(LOG_TAG, "высота экроана =" + heightScreen);

    }


    private  void readSharePreferences() {
        sPref = getPreferences(MODE_PRIVATE);
        float myLocLat = sPref.getFloat(MY_LOCATION_LAT, 0);
        float myLocLng = sPref.getFloat(MY_LOCATION_LNG, 0);
        Log.d(LOG_TAG, "readSharePreferences() Lat =" + myLocLat + "lng = " + myLocLng);
        myLoc = new LatLng(myLocLat, myLocLng);

    }

    private  void writeSharePreferences() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putFloat(MY_LOCATION_LAT, MyApplication.lat);
        ed.putFloat(MY_LOCATION_LNG, MyApplication.lng);
        Log.d(LOG_TAG, "writeSharePreferences() Lat =" + MyApplication.lat + "lng = " + MyApplication.lng);
        ed.commit();

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
        db = dbHelper.getWritableDatabase();
       // int sizeAlarm = 0;
        int i = 0;

        Cursor cursor = db.query("locations", new String[] {DatabaseHelper.ID,DatabaseHelper.NAME_COLUMN, DatabaseHelper.RUN,
                        DatabaseHelper.LATITUDE_COLUMN, DatabaseHelper.LONGITUDE_COLUMN},
                null, null,
                null, null, null) ;
       // alarmItem.clear();
        int size = MyApplication.alarmItem.size();
        if(size>0) {
            Log.d(LOG_TAG, "очищаем myApplication.alarmItem = " +  size);
            MyApplication.alarmItem.clear();
        }


        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
            String Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_COLUMN));
            float latitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.LATITUDE_COLUMN));
            float longitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.LONGITUDE_COLUMN));
            Boolean run = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RUN)) == 1);


                MyApplication.alarmItem.add(i, new GifItem(Name, latitude, longitude, id, run));

/*
            if(i<size) {
                MyApplication.alarmItem.get(i).update(Name, latitude, longitude, id, run);
            }
            else {
                MyApplication.alarmItem.add(i, new GifItem(Name, latitude, longitude, id, run));
            }
*/
            Log.d(LOG_TAG, "i=" + i + " id=" + id +" Name =" + Name + " longitude =" + longitude + " latitude =" + latitude);

            float[] res = new float[3];
            Location.distanceBetween(latitude, longitude, MyApplication.lat, MyApplication.lng, res);

            MyApplication.alarmItem.get(i).setDistance(res[0]);
            i++;
        }
        /*
        for( ; i<size; i++) {
            Log.d(LOG_TAG, "удаляем i=" + i);
            MyApplication.alarmItem.remove(i);

        }
        */
        // не забываем закрывать курсор
        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
       if(mMap!=null) updateMap();



    }

//удаление будильника
    public void deleteItem(final int id, final int i) {

        Log.d(LOG_TAG, "deleteItem i =" + i  + "id = " + id);

        final Handler h2 = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Log.d(LOG_TAG, "обновляем адаптер = ");
              //  adapter.notifyDataSetChanged();
              //  updateMap();
                readDatabase();
            };
        };

        Thread t = new Thread(new Runnable() {
            public void run() {
                db = dbHelper.getWritableDatabase();
                int delCount = db.delete(DatabaseHelper.DATABASE_TABLE, "id = " + id, null);
                Log.d(LOG_TAG, "deleted rows count = " + delCount);
                db.close();
                    h2.sendEmptyMessage(0);
             //   MyApplication.alarmItem.remove(i);
            }
        });
        t.start();
      //  readDatabase();
       //
      //  adapter.notifyDataSetChanged();


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onComplete(String name) {
        Log.d(LOG_TAG, "получили =" + name);
        currentNamePoint = name;
        choisePoint = true;
        animationMap();
       // lParams.height = RecyclerView.LayoutParams.MATCH_PARENT;

       // cardview.startAnimation(new ViewAnimation());

/*
        ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.scene_root);

        final Scene scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.content_main, this);

        TransitionSet set = new TransitionSet();
        set.addTransition(new Fade());
        set.addTransition(new ChangeBounds());
        // выполняться они будут одновременно
     //   set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        // уставим свою длительность анимации
        set.setDuration(1500);
        // и изменим Interpolator
        set.setInterpolator(new AccelerateInterpolator());
        TransitionManager.go(scene2, set);
*/
        //    ViewGroup cardview1 = (ViewGroup) findViewById(R.id.cardView);

    //    TransitionManager.beginDelayedTransition(cardview1);

       // newPoint(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void animationMap() {


        paramsLinear = (LinearLayout.LayoutParams) cardview.getLayoutParams();
        Log.d(LOG_TAG, "высота экроана2 " + paramsLinear.weight);
       // heightMap= params.height;
        //heightMap =paramsLinear.weight;

        tvchoisePoint.setVisibility(View.VISIBLE);

        linLayoutConteiner.animate()
                .setDuration(200)
                .setStartDelay(200)
                .translationYBy(50)
        .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ValueAnimator slideAnimator = ValueAnimator
                        .ofFloat(1.1f, 0.05f)
                        .setDuration(300);
                //rv.setVisibility(View.GONE);


                slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        // I'm going to set the layout's height 1:1 to the tick
                        //  params.height = FrameLayout.LayoutParams.MATCH_PARENT;
                        paramsLinear.weight = value.floatValue();
                        //   value.intValue();
                        cardview.requestLayout();
                    }
                });

                AnimatorSet set = new AnimatorSet();
                set.play(slideAnimator);
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


/*
        cardview.animate()
                .setDuration(2000)
        .y(20);

        params.height = heightMap;

        cardview.setLayoutParams(params);
        */
/*
        TransitionSet set = new TransitionSet();

        // set.addTransition(new ChangeImageTransform());
         set.addTransition(new Fade());
        Fade а = new Fade(Fade.IN);
        set.addTransition(new ChangeBounds());
        // выполняться они будут одновременно
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        // уставим свою длительность анимации
        set.setDuration(200);
        set.setStartDelay(100);

        TransitionManager.beginDelayedTransition(sceneRoot, set);

        // и применим сами изменения
      //  ViewGroup.LayoutParams params = cardview.getLayoutParams();
        //  params.width = newSquareSize;
      //  params.height = RecyclerView.LayoutParams.MATCH_PARENT;
        params.height = heightMap*2;

        cardview1.setLayoutParams(params);
*/

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void animationMap2() {

        linLayoutConteiner.animate()
                .setDuration(200)
                .setStartDelay(100)
                .translationYBy(-50)
        .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.d(LOG_TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.d(LOG_TAG, "onAnimationEnd");
               // rv.setVisibility(View.VISIBLE);
                tvchoisePoint.setVisibility(View.GONE);

                ValueAnimator slideAnimator = ValueAnimator
                        .ofFloat(0.1f, 1f)
                        .setDuration(300);
                slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // get the value the interpolator is at
                        Float value = (Float) animation.getAnimatedValue();
                        // I'm going to set the layout's height 1:1 to the tick
                        paramsLinear.weight = value.floatValue();
                        //   value.intValue();
                        // force all layouts to see which ones are affected by
                        // this layouts height change
                        cardview.requestLayout();
                    }
                });

                AnimatorSet set = new AnimatorSet();
// since this is the only animation we are going to run we just use
// play
                set.play(slideAnimator);
// this is how you set the parabola which controls acceleration
                set.setInterpolator(new AccelerateDecelerateInterpolator());
// start the animation
                set.start();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });





// create a new animationset


     //   ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.content_main);
   //     ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.container);
   //     View cardview =  sceneRoot.findViewById(R.id.cardView);
   //     AnimatorSet set1 = new AnimatorSet();
/*
        TransitionSet set = new TransitionSet();

        set.setDuration(500);
        set.setStartDelay(1000);

       // set.addTransition(new ChangeImageTransform());
        set.addTransition(new ChangeBounds());
       // set.addTransition(new AutoTransition());
      //  set.addTransition(new ChangeClipBounds());
        set.addTransition(new Fade());

        // выполняться они будут одновременно
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        // уставим свою длительность анимации

     //   TransitionManager.go(sceneRoot, set);
        set.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(LOG_TAG, "onTransitionStart ");

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.d(LOG_TAG, "onTransitionEnd ");
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                Log.d(LOG_TAG, "onTransitionCancel ");
            }

            @Override
            public void onTransitionPause(Transition transition) {
                Log.d(LOG_TAG, "onTransitionPause ");
            }

            @Override
            public void onTransitionResume(Transition transition) {
                Log.d(LOG_TAG, "onTransitionResume ");
            }
        });

        TransitionManager.beginDelayedTransition(sceneRoot, set);
        // и применим сами изменения
      //  ViewGroup.LayoutParams params = cardview.getLayoutParams();
        //  params.width = newSquareSize;
        params.height = heightMap;
        cardview1.setLayoutParams(params);
*/
    }

// новая точка для будильника, запись в базу
    public void newPoint(String name, double latitude, double longitude) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
              // Задайте значения для каждого столбца
        values.put(DatabaseHelper.NAME_COLUMN, name);
        values.put(DatabaseHelper.LATITUDE_COLUMN, latitude);
        values.put(DatabaseHelper.LONGITUDE_COLUMN, longitude);
        values.put(DatabaseHelper.RUN, 0);

        // Вставляем данные в таблицу
        db.insert("locations", null, values);
     //   MyApplication.alarmItem.add(new GifItem(name,  (float) latitude, (float) longitude, id, false));
     //   adapter.notifyDataSetChanged();
        db.close();
        readDatabase();

    }

    public void saveRun(int r, int idd) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String g = Integer.toString(idd);
        values.put(DatabaseHelper.RUN, r);
        // обновляем по id
        int updCount = db.update(DatabaseHelper.DATABASE_TABLE, values, "id = ?",
                new String[] { g } );
        db.close();
        Log.d(LOG_TAG, "updated rows count = " + updCount);
    }


    public void getDistance() {
        Log.d(LOG_TAG, "getDistance");
        getdistanceStart = true;
       handler = new Handler();

       runnableUpdateAdapter = new Runnable() {
            @Override
            public void run() {
           //     Log.d(LOG_TAG, "run"+ timeout);
                if(timeout<3000) timeout +=500;
                if(myService !=null) {
                    Log.d(LOG_TAG, "getDistance, notifyDataSetChanged()");
                    //   Log.d(LOG_TAG, "myService !=null getAlarmItem = " + myService.getAlarmItem().size());
                  //  Log.d(LOG_TAG, "getDistance  = " + myService.getDistance());
            //        myService.setAlarmItem(alarmItem);
//                    myService.checkConnection();
                    adapter.notifyDataSetChanged();
                  if(!choisePoint) drawLine(false);
                }
                handler.postDelayed(this, timeout);
            }
        };
        handler.post(runnableUpdateAdapter);
       // handler.removeCallbacks(runnableUpdateAdapter);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult requestCode = " + requestCode + ", resultCode = "
                + resultCode);
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
            openSetting = true;
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMapCamera() {


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(LOG_TAG, "onMapReady");
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMapClick(LatLng latLng) {
                //  Log.d(LOG_TAG, "кликнули на карту");

                if(choisePoint) {
                    //markerLoc = latLng;
                    marker = new MarkerOptions()
                            .position(latLng)
                            .title(currentNamePoint)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker2));
                    mMap.addMarker(marker);
                    newPoint(currentNamePoint, latLng.latitude, latLng.longitude);
                    Log.d(LOG_TAG, "кликнули на карту=" + latLng.latitude + " long=" + latLng.longitude);
                    choisePoint = false;
                  //  lParams.height = heightMap;

                    animationMap2();

                }
                else {
                    moveMapCamera();
                }

            }
        });


        updateMap();

    }


    public  void moveMapCamera() {
        Log.d(LOG_TAG, " LatLngBounds.Builder builder = " );
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
        for(int i = 0; i < MyApplication.alarmItem.size(); i++) {
            LatLng latLng = new LatLng(MyApplication.alarmItem.get(i).getlatitude(), MyApplication.alarmItem.get(i).getLongitude());
            MarkerOptions  marker = (new MarkerOptions().position(latLng));
            markers.add(marker);
        }
        if(myLoc!= null) {
            markers.add(new MarkerOptions().position(myLoc));
        }

        if(MyApplication.alarmItem.size()>0) {
            for (MarkerOptions marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 70; // offset from edges of the map in pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    public void drawLine(boolean updateline) {

      //  boolean allmarker = true;

       if (polylineArrayList.size()>0) {

           for (Polyline polyline : polylineArrayList) {
              polyline.remove();
           }
       }

        if (circleArrayList.size()>0 && updateline) { // для того чтобы круг не перерисовывался
            for (Circle circle : circleArrayList) {
                circle.remove();
            }
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();

        for(int j = 0; j < MyApplication.alarmItem.size(); j++) {

            if (MyApplication.alarmItem.get(j).getRun()) {
/*
                PolylineOptions rectOptions = new PolylineOptions()
                        .add(new LatLng(MyApplication.alarmItem.get(j).getlatitude(), MyApplication.alarmItem.get(j).getLongitude()))
                        .add(new LatLng(MyApplication.lat, MyApplication.lng))
                        .color(Color.BLUE)
                        .width(4);



                 if(polylineArrayList != null && mMap != null)   polylineArrayList.add(mMap.addPolyline(rectOptions));

*/
                // String radius = sp.getString(getResources().getString(R.string.radius), "100");
                int radius = Integer.parseInt(sp.getString(getResources().getString(R.string.radius), "100"));

                if(updateline) {
                    CircleOptions circleOptions = new CircleOptions()
                            .center(new LatLng(MyApplication.alarmItem.get(j).getlatitude(), MyApplication.alarmItem.get(j).getLongitude()))
                            .radius(radius)
                            .fillColor(Color.argb(102, 57, 202, 232))
                            .strokeWidth(1)
                            .strokeColor(Color.argb(0, 0, 0, 0));

                    if(circleArrayList != null) circleArrayList.add(mMap.addCircle(circleOptions));
                }


              //  circle = mMap.addCircle(circleOptions);

                LatLng latLng = new LatLng(MyApplication.alarmItem.get(j).getlatitude(), MyApplication.alarmItem.get(j).getLongitude());
                MarkerOptions marker = (new MarkerOptions().position(latLng));
                markers.add(marker);

            }
        }
        if(markers.size()<1) {
            for(int j = 0; j < MyApplication.alarmItem.size(); j++) {
                LatLng latLng = new LatLng(MyApplication.alarmItem.get(j).getlatitude(), MyApplication.alarmItem.get(j).getLongitude());
                MarkerOptions marker = (new MarkerOptions().position(latLng));
                markers.add(marker);
            }
        }
        if(MyApplication.alarmItem.size()>0) {
            markers.add(new MarkerOptions().position(new LatLng(MyApplication.lat, MyApplication.lng)));
            for (MarkerOptions marker1 : markers) {
                builder.include(marker1.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 60; // offset from edges of the map in pixels
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            } else {

            }
        }

    }


    public void updateMap() {
        mMap.clear();
        for(int i = 0; i < MyApplication.alarmItem.size(); i++) {
            LatLng latLng = new LatLng(MyApplication.alarmItem.get(i).getlatitude(), MyApplication.alarmItem.get(i).getLongitude());
            marker = new MarkerOptions().position(latLng)
                    .title(MyApplication.alarmItem.get(i).getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker2));
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
        openSetting = false;
        super.onResume();
        mapView.onResume();

            if(myService!= null) {
                String radius = sp.getString(getResources().getString(R.string.radius), "100");
                int r = Integer.parseInt(radius);
                myService.setRadius(r);
                drawLine(true); //после обновления настроект перерисовываем круг
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

       // if(!openSetting)
            myService.setStopSelf(true);
        Log.d(LOG_TAG, "onDestroy");

    //    mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
     //   mGoogleApiClient.connect();
        Log.d(LOG_TAG, "onStart");
     /*   mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
*/
       // Intent intent = new Intent(this, MyService.class);

        bindService(intent, connection, BIND_AUTO_CREATE); //Context.BIND_AUTO_CREATE


        if (!getdistanceStart) getDistance();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    //    AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction0());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
      //  AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction0());
        Log.d(LOG_TAG, "onStop");
        handler.removeCallbacks(runnableUpdateAdapter);
        writeSharePreferences();
        getdistanceStart = false;

        if (bound) {
            Log.d(LOG_TAG, "onStop bound = " + bound);
           // if(!openSetting)
          //  myService.setStopSelf(true);
           // Log.d(LOG_TAG, "onStopmyService.setStopSelf(true)" );
            unbindService(connection);
            bound = false;

        }

     //   mGoogleApiClient.disconnect();
    }



}
