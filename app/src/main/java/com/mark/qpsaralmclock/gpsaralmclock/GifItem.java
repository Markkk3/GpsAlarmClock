package com.mark.qpsaralmclock.gpsaralmclock;


public class GifItem {

    String name;
    float latitude;
    float longitude;
    int id;
    float distans = 0;
    boolean run = false;


    GifItem(String n, float lat, float longit, int id1) {
        name = n;
        latitude = lat;
        longitude = longit;
        id = id1;
    }

    public float getlatitude() {
        return  latitude;
    }

    public float getLongitude() {
        return  longitude;
    }

    public String getName() {
        return  name;
    }

    public int getId() {  return id;  }

    public float getDistance() {  return distans;  }

    public void setDistance(float d) {  distans=d;  }

    public boolean getRun() {  return run;  }

    public void setRun(boolean r) {  run=r;  }

}
