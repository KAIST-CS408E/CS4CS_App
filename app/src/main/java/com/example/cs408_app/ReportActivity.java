package com.example.cs408_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerClickListener, ResultCallback<Status>, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    Marker currentMarker;
    int radius = 100;
    int REQUEST_LOCATION = 1;
    int REQUEST_LOCATION_GEOFENCE = 2;

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
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this,this)
                .build();
        client.connect();
        // Add a marker in EE building and move the camera
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
/*
        LatLng CSBldg = new LatLng(36.368299, 127.364844);
        if (currentMarker!=null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(CSBldg).title("Marker in CSBldg"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CSBldg, 17.0f)); // 16.0f~18.0f*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
            }
            else {
                System.out.printf("Permission denied.");
            }
        }
        else if (requestCode == REQUEST_LOCATION_GEOFENCE) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationServices.GeofencingApi.addGeofences(client, geoRequest, createGeofencePendingIntent());
            }
            else {
                System.out.printf("Permission denied.");
            }
        }
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
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected location"));
        mMap.moveCamera(update);
    }

    @Override
    public void onResult(@NonNull Status status) {
        //drawGeofence();
    }

    Circle geoFenceLimits;

    private void drawGeofence() {
        if (geoFenceLimits != null) {
            geoFenceLimits.remove();
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(currentMarker.getPosition())
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(radius);

        geoFenceLimits = mMap.addCircle(circleOptions);
    }

    GeofencingRequest geoRequest;

    @Override
    public boolean onMarkerClick(Marker marker) {
        // if the marker is clicked, make an Geofence
        if (currentMarker != null) {
            Geofence geofence = createGeofence(currentMarker.getPosition(), radius);
            geoRequest = createGeoRequest(geofence);
            //addGeofence(geofence);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_GEOFENCE);
            }
            LocationServices.GeofencingApi.addGeofences(client, geoRequest, createGeofencePendingIntent());
        }
        return false;
    }

    PendingIntent geofencePendingIntent;
    private PendingIntent createGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceIntentService.class);
        drawGeofence();
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Geofence createGeofence(LatLng position, float radius) {
        return new Geofence.Builder()
                .setRequestId("My Geofence")
                .setCircularRegion(position.latitude, position.longitude, radius) //Math.max(radius,100)
                .setExpirationDuration(600 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER| Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    // Specifies the list of geofences to be monitored and how the geofence notifications should be reported.
    // getGeofences() returns List<Geofence>
    private GeofencingRequest createGeoRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                // trigger a notification at the moment when the geofence is added and if the device is already inside that geofence.
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

}
