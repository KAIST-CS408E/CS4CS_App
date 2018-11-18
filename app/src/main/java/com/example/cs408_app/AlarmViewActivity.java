package com.example.cs408_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Adapter.AlarmRecyclerAdapter;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.Model.User;
import com.example.cs408_app.Model.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlarmViewActivity extends AppCompatActivity {

    TextView textView;
    AlarmElement oAlarm;
    SharedPreferences preferences;
    private CS4CSApi apiService;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);
        Bundle args = getIntent().getExtras();

        preferences = getSharedPreferences("register", MODE_PRIVATE);

        oAlarm = (AlarmElement) args.getSerializable("alarm");

        textView = findViewById(R.id.text_title);
        textView.setText(oAlarm.getTitle());

        textView = findViewById(R.id.text_desc);
        textView.setText(oAlarm.getDesc());

        textView = findViewById(R.id.text_date);
        textView.setText(oAlarm.getCreated_at());

        textView = findViewById(R.id.text_cat);
        textView.setText(oAlarm.getCat_str());

        Boolean is_official = preferences.getBoolean("is_official", false);
        View v = (View)findViewById(R.id.include_reporter_info);
        if(is_official){
            textView = (TextView) v.findViewById(R.id.reporter_name);
            displayReporterProfile(oAlarm.get_id());
        }
        else{
            v.setVisibility(View.GONE);
        }

    }

    private void displayReporterProfile(String alarm_id){
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
        apiService = retrofit.create(CS4CSApi.class);
        Call<UserProfile> call = apiService.getReporterProfile(alarm_id);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                textView.setText(response.body().getName());
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.d("AlarmView","failed to get reporter info");
            }
        });
    }
}
