package com.example.cs408_app.Model;

public class AnnounceElement {
    String _id, alarm_id, contents, created_at;

    public AnnounceElement(String _id, String alarm_id, String contents, String created_at) {
        this._id = _id;
        this.alarm_id = alarm_id;
        this.contents = contents;
        this.created_at = created_at;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(String alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
