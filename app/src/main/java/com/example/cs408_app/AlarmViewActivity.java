package com.example.cs408_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.Model.UserProfile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlarmViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    AlarmElement oAlarm;
    SharedPreferences preferences;
    private CS4CSApi apiService;
    private Retrofit retrofit;

    TextView textView;
    Button callBtn;
    Button button;
    ImageView imageView;

    private  void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Double lat = oAlarm.getLat();
        Double lng = oAlarm.getLng();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 17F));
        Marker mMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
        googleMap.addCircle(new CircleOptions()
                .center(mMarker.getPosition()).radius(oAlarm.getRad())
                .fillColor(0x80F46542).strokeWidth(0F));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);
        Bundle args = getIntent().getExtras();

        preferences = getSharedPreferences("register", MODE_PRIVATE);

        oAlarm = (AlarmElement) args.getSerializable("alarm");
        initMap();

        textView = findViewById(R.id.text_name);
        textView.setText(oAlarm.getTitle());

        textView = findViewById(R.id.text_desc);
        textView.setText(oAlarm.getDesc());

        final String OLD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        final String NEW_FORMAT = "MM월 dd일 HH:mm";

        // August 12, 2010
        String oldDateString = oAlarm.getCreated_at();
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);

        textView = findViewById(R.id.text_date);
        textView.setText(newDateString);

        textView = findViewById(R.id.text_cat);
        textView.setText("Type: " + oAlarm.getCat_str());

        imageView = findViewById(R.id.pictogram);
        if (oAlarm.getCat_str().equals("Fire"))
            imageView.setImageResource(R.drawable.picflam);
        else if (oAlarm.getCat_str().equals("Explosion"))
            imageView.setImageResource(R.drawable.picexplo);
        else if (oAlarm.getCat_str().equals("Chemical"))
            imageView.setImageResource(R.drawable.picskull);
        else if (oAlarm.getCat_str().equals("Bio"))
            imageView.setImageResource(R.drawable.picsilho);
        else if (oAlarm.getCat_str().equals("Corrosion"))
            imageView.setImageResource(R.drawable.picacid);

        Boolean is_official = preferences.getBoolean("is_official", false);
        View v = (View)findViewById(R.id.include_reporter_info);
        if(is_official){
            textView = (TextView) v.findViewById(R.id.reporter_name);
            callBtn = (Button) v.findViewById(R.id.button_call_reporter);
            displayReporterProfile(oAlarm.get_id());
        }
        else{
            v.setVisibility(View.GONE);
        }

        //comment activity
        button = findViewById(R.id.button_comment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserCommentActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("alarm", oAlarm);
                intent.putExtras(args);
                startActivity(intent);
            }
        });

        //announce activity
        button = findViewById(R.id.button_announce);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnnounceActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("alarm", oAlarm);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
    }

    private void displayReporterProfile(String alarm_id){
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
        apiService = retrofit.create(CS4CSApi.class);
        Call<UserProfile> call = apiService.getReporterProfile(alarm_id);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                final UserProfile reporter = response.body();
                textView.setText(reporter.getName());
                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phone = reporter.getPhone_number();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.d("AlarmView","failed to get reporter info");
            }
        });
    }
}
