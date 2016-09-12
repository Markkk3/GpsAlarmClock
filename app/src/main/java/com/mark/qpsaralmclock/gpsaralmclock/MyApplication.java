package com.mark.qpsaralmclock.gpsaralmclock;

import android.app.Application;

import java.util.ArrayList;


public class MyApplication extends Application {

    private static MyApplication singleton;
    public static ArrayList<GifItem> alarmItem;
    public static float lat;
    public static float lng;

    // Returns the application instance
    public static MyApplication getInstance() {
        return singleton;
    }

    public final void onCreate() {
        super.onCreate();
        singleton = this;

        alarmItem = new ArrayList<GifItem>();
    }
}
