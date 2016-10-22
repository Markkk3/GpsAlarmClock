package com.mark.gpsalarmclock;

import android.app.Application;

import com.bettervectordrawable.VectorDrawableCompat;
import com.mark.qpsalarmclock.R;

import java.util.ArrayList;


public class MyApplication extends Application {

    private static MyApplication singleton;
    public static ArrayList<GifItem> alarmItem;
    public static float lat;
    public static float lng;
    public static Thread myThread;

    // Returns the application instance
    public static MyApplication getInstance() {
        return singleton;
    }

    public final void onCreate() {
        super.onCreate();
        int[] ids = VectorDrawableCompat.findAllVectorResourceIdsSlow(getResources(), R.drawable.class);
        VectorDrawableCompat.enableResourceInterceptionFor(getResources(), ids);

        singleton = this;

        alarmItem = new ArrayList<GifItem>();


    }


}
