package com.example.cs408_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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
import com.example.cs408_app.Adapter.RecyclerItemClickListener;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.AlarmElement;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AllAlarmsActivity extends AppCompatActivity {

    Retrofit retrofit;
    CS4CSApi apiService;
    TextView textView;

    private LinearLayoutManager layoutManager;
    List<AlarmElement> alarmList = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alarms);

        Button refresh_btn = findViewById(R.id.refresh_button);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlarmList();
            }
        });
        getAlarmList();
        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(AllAlarmsActivity.this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(AllAlarmsActivity.this, AlarmViewActivity.class);
                        AlarmElement selected = alarmList.get(position);
                        Bundle args = new Bundle();
                        args.putString("title", selected.getTitle());
                        intent.putExtras(args);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // nothing much currently
                    }
                }));
    }

    private void getAlarmList(){
        try{
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
            apiService = retrofit.create(CS4CSApi.class);
            Call<List<AlarmElement>> call = apiService.getAlarmList();
            call.enqueue(new Callback<List<AlarmElement>>() {
                @Override
                public void onResponse(Call<List<AlarmElement>> call, Response<List<AlarmElement>> response) {
                    alarmList = response.body();
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
                    layoutManager = new LinearLayoutManager(AllAlarmsActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    AlarmRecyclerAdapter alarmRVA = new AlarmRecyclerAdapter(getApplicationContext(), alarmList);
                    recyclerView.setAdapter(alarmRVA);
                }

                @Override
                public void onFailure(Call<List<AlarmElement>> call, Throwable t) {
                    Log.d("AllAlarms","failed to get alarm list");
                }
            });
        }
        catch(Exception e){
            Log.d("AllAlarms", "failed to get alarm list");
        }
    }
}
