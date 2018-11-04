package com.example.cs408_app.Model;

/**
 * Created by 권태형 on 2018-11-04.
 */

public class Alarm{
    double lat, lng, rad;

    public  Alarm(double lat, double lng, double radius){
        this.lat = lat;
        this.lng = lng;
        this.rad = radius;
    }
}