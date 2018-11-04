package com.example.cs408_app;

import com.example.cs408_app.Model.Alarm;
import com.example.cs408_app.Model.Response;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by 권태형 on 2018-11-04.
 */

public interface ApiService {
    public static final String API_URL = "http://172.30.1.52:8003";
    @POST("/alarm")
    Call<Response> postAlarm(@Body Alarm alarm);
}


