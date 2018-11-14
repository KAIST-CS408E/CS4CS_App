package com.example.cs408_app.Model;

import java.io.Serializable;

public class AlarmElement implements Serializable {
    private double lat, lng, rad;
    private String title, cat_str, desc, reporter_id, created_at;

    public AlarmElement(double lat, double lng, double rad, String title, String cat_str, String desc, String reporter_id, String created_at){
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
        this.title = title;
        this.cat_str = cat_str;
        this.desc = desc;
        this.reporter_id = reporter_id;
        this.created_at = created_at;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getRad() {
        return rad;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCat_str() {
        return cat_str;
    }

    public void setCat_str(String cat_str) {
        this.cat_str = cat_str;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReporter_id() {
        return reporter_id;
    }

    public void setReporter_id(String reporter_id) {
        this.reporter_id = reporter_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
