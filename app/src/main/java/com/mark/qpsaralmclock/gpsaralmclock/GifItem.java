package com.mark.qpsaralmclock.gpsaralmclock;


public class GifItem {

    String name;
    float latitude;
    float longitude;


    GifItem(String n, float lat, float longit) {
        name = n;
        latitude = lat;
        longitude = longit;
    }

    public float Getlatitude() {
        return  latitude;
    }

    public float GetLongitude() {
        return  longitude;
    }

    public String GetName() {
        return  name;
    }
}
