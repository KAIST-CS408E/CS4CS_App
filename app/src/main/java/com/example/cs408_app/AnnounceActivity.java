package com.example.cs408_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Adapter.AnnounceRecyclerAdapter;
import com.example.cs408_app.Adapter.CommentRecyclerAdapter;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.Model.Announce;
import com.example.cs408_app.Model.AnnounceElement;
import com.example.cs408_app.Model.Comment;
import com.example.cs408_app.Model.CommentElement;

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

public class AnnounceActivity extends AppCompatActivity {

    AlarmElement oAlarm;
    Retrofit retrofit;
    CS4CSApi apiService;
    TextView textView;
    EditText editText;
    Button button;
    SharedPreferences preferences;

    List<AnnounceElement> announceList = null;
    private RecyclerView recycler;
    AnnounceRecyclerAdapter adapter;


    final String TAG = "AnnounceActivity";

    public void makeAnnounce(Announce announce){
        Call<com.example.cs408_app.Model.Response> call = apiService.makeAnnounce(announce, oAlarm.get_id());
        call.enqueue(new Callback<com.example.cs408_app.Model.Response>() {
            @Override
            public void onResponse(Call<com.example.cs408_app.Model.Response> call, Response<com.example.cs408_app.Model.Response> response) {
                Toast.makeText(getApplicationContext(), "Made comment", Toast.LENGTH_LONG).show();
                getAnnounceList(); //refresh after sending
            }

            @Override
            public void onFailure(Call<com.example.cs408_app.Model.Response> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        Bundle args = getIntent().getExtras();
        oAlarm = (AlarmElement) args.getSerializable("alarm");

        textView = findViewById(R.id.text_name);
        textView.setText(oAlarm.getTitle());

        editText = findViewById(R.id.text_announce);
        button = findViewById(R.id.button_announce);

        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively
        Boolean is_offical = preferences.getBoolean("is_official", false);
        if(!is_offical){
            View v = findViewById(R.id.announce_dock);
            v.setVisibility(View.GONE);
        }

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
        apiService = retrofit.create(CS4CSApi.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contents = editText.getText().toString();
                editText.setText(""); // clear comment edit text
                makeAnnounce(new Announce(contents));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAnnounceList();
    }

    private void getAnnounceList(){
        recycler = findViewById(R.id.recycler);
        announceList = new ArrayList<>();
        adapter = new AnnounceRecyclerAdapter(AnnounceActivity.this, announceList, oAlarm.get_id());
        LinearLayoutManager layoutManager = new LinearLayoutManager(AnnounceActivity.this);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Get comment list from the server
        try{
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
            apiService = retrofit.create(CS4CSApi.class);
            Call<List<AnnounceElement>> call = apiService.getAnnounceList(oAlarm.get_id());
            call.enqueue(new Callback<List<AnnounceElement>>() {
                @Override
                public void onResponse(Call<List<AnnounceElement>> call, Response<List<AnnounceElement>> response) {
                    announceList = response.body();
                    Collections.sort(announceList, new sortByCreatedAt());
                    recycler.setAdapter(new AnnounceRecyclerAdapter(getApplicationContext(), announceList, oAlarm.get_id()));
                    recycler.smoothScrollToPosition(0);
                    /*
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    */
                }

                @Override
                public void onFailure(Call<List<AnnounceElement>> call, Throwable t) {
                    Log.d("AllAlarms","failed to get alarm list");
                    Toast.makeText(AnnounceActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e){
            Log.d("AllAlarms", "failed to get alarm list");
        }

    }

    class sortByCreatedAt implements Comparator<AnnounceElement> {
        @Override
        public int compare(AnnounceElement a, AnnounceElement b) {
            Date a_date, b_date;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.KOREA);

            try {
                a_date = format.parse(a.getCreated_at());
                b_date = format.parse(b.getCreated_at());
                return (a_date.getTime() > b_date.getTime()) ? 1 : (a_date.getTime() < b_date.getTime()) ? -1 : 0;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }

        }
    }
}
