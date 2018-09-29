package com.example.cs408_app;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 권태형 on 2018-09-29.
 */

public class GeofenceIntentService extends IntentService {

    public GeofenceIntentService() {
        super("GeofenceIntentService");
    }

    // ...
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String error = String.valueOf(geofencingEvent.getErrorCode());
            Toast.makeText(getApplicationContext(), "Error code = " + error, Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            ArrayList<String> idsList = new ArrayList<>();
            for(Geofence geofence : triggeringGeofences) {
                idsList.add(geofence.getRequestId());
            }

            String status = null;
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                status = "Entering";
            }
            else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                status = "Exiting";
            }

            String geoTransitionDetails =  status + TextUtils.join(": ", idsList);
            Toast.makeText(getApplicationContext(), geoTransitionDetails, Toast.LENGTH_SHORT).show();
        } else {
            // Log the error.
            Log.e("Another Event", Integer.toString(geofenceTransition));
        }
    }
}
