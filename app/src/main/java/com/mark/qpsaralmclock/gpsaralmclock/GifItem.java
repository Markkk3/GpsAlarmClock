package com.mark.qpsaralmclock.gpsaralmclock;


public class GifItem {

    String name;
    float latitude;
    float longitude;
    int id;
    float distans = 0;
    boolean run = false;


    GifItem(String n, float lat, float longit, int id1, int r) {
        name = n;
        latitude = lat;
        longitude = longit;
        id = id1;
       if(r==1) run=true;
        else run =false;
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
