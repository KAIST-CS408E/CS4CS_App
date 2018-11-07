package com.example.cs408_app.Model;

/**
 * Created by 권태형 on 2018-11-04.
 */

public class Alarm{
    double lat, lng, rad;
    String title, cat_str, desc, reporter;

    public  Alarm(double lat, double lng, double radius, String title, String cat_str, String desc, String reporter){
        this.lat = lat;
        this.lng = lng;
        this.rad = radius;
        this.title = title;
        this.cat_str = cat_str;
        this.desc = desc;
        this.reporter = reporter;
    }
}