package com.example.cs408_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Adapter.AlarmRecyclerAdapter;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.AlarmElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AllAlarmsActivity extends AppCompatActivity {

    Retrofit retrofit;
    CS4CSApi apiService;
    TextView textView;

    List<AlarmElement> alarmList = null;
    private RecyclerView recycler;
    AlarmRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alarms);

        getAlarmList();

        recycler = findViewById(R.id.recycler);
    }

    private void getAlarmList(){

        // Setting Recycler View, Swipe Refresh, Default Layout and Empty Adapter
        recycler = findViewById(R.id.recycler);
        alarmList = new ArrayList<>();
        adapter = new AlarmRecyclerAdapter(AllAlarmsActivity.this, alarmList);

        recycler.setLayoutManager(new LinearLayoutManager(AllAlarmsActivity.this));
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // any View reflecting the data has been changed should refresh itself

        swipeContainer = findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark));
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAlarmList();
                Toast.makeText(AllAlarmsActivity.this, "Alarm List Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        // Get AlarmList from the server
        try{
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
            apiService = retrofit.create(CS4CSApi.class);
            Call<List<AlarmElement>> call = apiService.getAlarmList();
            call.enqueue(new Callback<List<AlarmElement>>() {
                @Override
                public void onResponse(Call<List<AlarmElement>> call, Response<List<AlarmElement>> response) {
                    alarmList = response.body();
                    Collections.sort(alarmList, new sortByCreatedAt());
                    recycler.setAdapter(new AlarmRecyclerAdapter(getApplicationContext(), alarmList));
                    recycler.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<List<AlarmElement>> call, Throwable t) {
                    Log.d("AllAlarms","failed to get alarm list");
                    Toast.makeText(AllAlarmsActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e){
            Log.d("AllAlarms", "failed to get alarm list");
        }
    }

    /**
     * Definition of "which is the first alarm post, second, .."
     */
    class sortByCreatedAt implements Comparator<AlarmElement> {
        @Override
        public int compare(AlarmElement a, AlarmElement b) {
            // Uploaded date of an alarm post
            Date a_date, b_date;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.KOREA);

            try {
                a_date = format.parse(a.getCreated_at());
                b_date = format.parse(b.getCreated_at());
                return (a_date.getTime() > b_date.getTime()) ? -1 : (a_date.getTime() < b_date.getTime()) ? 1 : 0;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }

        }
    }
}
