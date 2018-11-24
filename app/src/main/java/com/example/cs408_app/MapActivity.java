package com.example.cs408_app;


import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;


/**
 * Created by 권태형 on 2018-11-01.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, SeekBar.OnSeekBarChangeListener{

    private GoogleMap mMap;
    private Marker mMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 17F;
    private static final String TAG = "MapActivity";
    private boolean block_map_click = false;
    private Circle circle;
    Double myLat, myLng;

    private  void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

        //Log.e("TAG",Integer.toString(progress));
        circle.setRadius(progress);
        Log.e(TAG, Integer.toString(progress));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Button ok_button = findViewById(R.id.ok);
        Button cancel_button = findViewById(R.id.cancel);
        Button send_button = findViewById(R.id.send);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(200);
        seekBar.setOnSeekBarChangeListener(this);

        Intent from_main = getIntent();
        myLat = from_main.getDoubleExtra("Latitude", 36.372);
        myLng = from_main.getDoubleExtra("Longitude", 127.363);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, SendReportActivity.class);
                LatLng position = mMarker.getPosition();

                double Lat = position.latitude;
                double Lng = position.longitude;

                intent.putExtra("Latitude", Lat);
                intent.putExtra("Longitude", Lng);
                Log.e(TAG,Double.toString(circle.getRadius()));
                intent.putExtra("Radius", circle.getRadius());

                startActivity(intent);
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                view.setVisibility(View.GONE);
                findViewById(R.id.seekBar).setVisibility(View.VISIBLE);
                findViewById(R.id.send).setVisibility(View.VISIBLE);
                findViewById(R.id.start).setVisibility(View.VISIBLE);
                findViewById(R.id.end).setVisibility(View.VISIBLE);

                circle = mMap.addCircle(new CircleOptions()
                        .center(mMarker.getPosition()).radius(50)
                        .fillColor(0x80F46542).strokeWidth(0F));
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMarker.remove();
                mMarker = null;

                view.setVisibility(View.INVISIBLE);
                findViewById(R.id.ok).setVisibility(View.INVISIBLE);
                findViewById(R.id.send).setVisibility(View.GONE);
                findViewById(R.id.seekBar).setVisibility(View.INVISIBLE);
                findViewById(R.id.start).setVisibility(View.INVISIBLE);
                findViewById(R.id.end).setVisibility(View.INVISIBLE);

                if (circle != null)
                    circle.remove();

                mMap.getUiSettings().setAllGesturesEnabled(true);
                block_map_click = false;
            }
        });

        initMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat,myLng), DEFAULT_ZOOM));
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (!block_map_click) {
            if (mMarker != null)
                mMarker.remove();

            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng));

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            block_map_click = true;

            findViewById(R.id.ok).setVisibility(View.VISIBLE);
            findViewById(R.id.cancel).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onStop () {
        super.onStop();;
        Log.e(TAG,"onStop");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}
