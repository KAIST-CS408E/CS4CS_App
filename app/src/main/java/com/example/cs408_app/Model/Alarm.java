package com.example.cs408_app.Model;

/**
 * Created by 권태형 on 2018-11-04.
 */

public class Alarm{
    double lat, lng, rad;
    String title, cat_str, desc, reporter;
    int floor, room_number;

    public  Alarm(double lat, double lng, double radius, String title, String cat_str, String desc, String reporter, int floor, int room_number){
        this.lat = lat;
        this.lng = lng;
        this.rad = radius;
        this.title = title;
        this.cat_str = cat_str;
        this.desc = desc;
        this.reporter = reporter;
        this.floor = floor;
        this.room_number = room_number;
    }
}