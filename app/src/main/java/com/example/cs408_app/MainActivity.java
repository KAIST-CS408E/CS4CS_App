package com.example.cs408_app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.R.drawable;
import android.widget.Toolbar;

import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.MyFirebaseMessagingService.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    // Shared Preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String TAG = "MainActiviy";
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // Google Map
    private Boolean mLocationPermissionGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 17F;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button;
        Switch officialSw;

        // Retrieve and hold the contents of the preferences file "register"
        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively

        getLocationPermission();

        button = findViewById(R.id.alarm);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseMessaging.getInstance().subscribeToTopic("alarm")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "subscribeSuccess";
                                if (!task.isSuccessful())
                                    msg = "subscribeFail";

                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        });

        button = findViewById(R.id.not_alarm);
        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("alarm")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "unSubscribeSuccess";
                                if (!task.isSuccessful())
                                    msg = "unSubscribeFail";

                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        button = findViewById(R.id.button_map);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.button_all_alarms);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllAlarmsActivity.class);
                startActivity(intent);
            }
        });

        Boolean is_registered = preferences.getBoolean("is_registered", false);
        if (!is_registered) {
            Toast.makeText(this, "Not registered!", Toast.LENGTH_SHORT).show();
            if (Constants.cheat_login) {
                preferences.edit().putString("user_email", Constants.cheat_email).apply();
            } else {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                // remove the activity stack ("go back" button will close the app, not return to main activity)
                finish();
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show();
        }


        officialSw = findViewById(R.id.official_switch);
        final Boolean is_official = preferences.getBoolean("is_official", false);
        officialSw.setChecked(is_official);
        officialSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("is_official", b).apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv = findViewById(R.id.text_user);
        String user_email = preferences.getString("user_email", "NO USER EMAIL");
        tv.setText("User: " + user_email);
    }

    private void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        COURSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            initMap();
            mLocationPermissionGranted = true;
        }

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            //mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(this);
    }

    private void getDeviceLocation(){

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{

            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){

                        Location currentLocation = (Location) task.getResult();
                        Double lat = 36.372, lng = 127.363;

                        if (currentLocation != null) {
                            lat = currentLocation.getLatitude();
                            lng = currentLocation.getLongitude();
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), DEFAULT_ZOOM));
                    }
                    else{
                        LatLng sydney = new LatLng(-34, 151);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }
}
