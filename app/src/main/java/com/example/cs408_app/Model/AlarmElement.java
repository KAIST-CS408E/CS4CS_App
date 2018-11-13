package com.example.cs408_app.Model;

public class AlarmElement {
    private String title;
    private String desc;
    private String reporter_id;

    public AlarmElement(String title, String desc, String reporter_id){
        this.title = title;
        this.desc = desc;
        this.reporter_id = reporter_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
