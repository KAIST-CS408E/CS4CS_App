package com.example.cs408_app;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this).build();
        client.connect();
        // Add a marker in EE building and move the camera
        mMap.setOnMapClickListener(this);
/*
        LatLng CSBldg = new LatLng(36.368299, 127.364844);
        if (currentMarker!=null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(CSBldg).title("Marker in CSBldg"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CSBldg, 17.0f)); // 16.0f~18.0f*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "location could not be found", Toast.LENGTH_SHORT).show();
        } else {
            if (currentMarker == null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17.0f);
                currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in your current location"));
                mMap.moveCamera(update);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17.0f);
        if (currentMarker!=null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected location"));
        mMap.moveCamera(update);
    }
}
