package com.example.cs408_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.cs408_app.Model.AlarmElement;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "FirebaseService";
    private int NOTIFICATION_ID = 1;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }

    private void CompareLocation(final Map<String, String> data, final Location acciLocation){

        try {
            FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){

                        Location currentLocation = (Location) task.getResult();

                        if (currentLocation != null){
                            float distance = currentLocation.distanceTo(acciLocation);

                            Log.e("CompareLocation", data.get("title") + ":" + Float.toString(distance) +", "
                            + data.get("rad"));

                            if (distance < Double.parseDouble(data.get("rad")))
                                sendNotification(data, true);
                            else if (data.get("first").equals("true"))
                                sendNotification(data, false);
                        }
                        else
                            Log.e(TAG, "Phone should turn on the location tracking");
                    }
                }
            });

        }catch (SecurityException e){
            Log.e(TAG, "App should require permission");
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        Map<String, String> data;
        Location acciLocation;
        data = remoteMessage.getData();
        Log.e("after messgae received", data.get("title"));

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Double lat = Double.parseDouble(data.get("lat"));
            Double lng = Double.parseDouble(data.get("lng"));
            //Double alt = Double.parseDouble(data.get("alt"));

            acciLocation = new Location("acciLocation");

            acciLocation.setLatitude(lat);
            acciLocation.setLongitude(lng);
            CompareLocation(data, acciLocation);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private Intent buildIntent(Map<String, String> data){

        AlarmElement received = new AlarmElement(data.get("id"), Double.parseDouble(data.get("lat")),
                Double.parseDouble(data.get("lng")), Double.parseDouble(data.get("rad")), data.get("title"), data.get("cat_str"), data.get("desc"),
                data.get("reporter_id"), data.get("created_at"));

        Bundle args = new Bundle();
        args.putSerializable("alarm", received);

        Intent intent = new Intent(this, AlarmViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(args);
        return intent;
    }

    private NotificationChannel makeChannel(String channel_id, Uri soundUri){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1500, 500, 1500, 500});
            notificationChannel.setShowBadge(true);

            notificationChannel.setSound(soundUri, null);
            return notificationChannel;
        }

        return null;
    }

    private NotificationCompat.Builder buildNotification(String channel_id, Map<String, String> data,
                                                         PendingIntent registerPendingIntent, boolean near){

        String cat_str;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id)
                // Show notification even on lock screen
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                // off-vibrate time(ms), on-vibrate time, off time, on time, off, on, ...
                .setVibrate(new long[] {0, 1500, 500, 1500, 500})
                // Icon size
                .setSmallIcon(R.mipmap.ic_launcher)// using mipmap, produce smaller icon
                // Contents
                .setContentTitle(data.get("title"))
                .setContentText(data.get("desc"))
                // When a user expands(slide down) original sized notification(above contents), show more info
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(data.get("body")))
                // If user click the notification,
                .setContentIntent(registerPendingIntent)
                .setAutoCancel(true)
                // If user click a button after expanding the notification
                .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark, "Show accident", registerPendingIntent))
                .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark, "Ignore", registerPendingIntent));

        if (near)
            // use custom mp3 sound file (./res/raw/siren.mp3)
            notificationBuilder.setSound(Uri.parse("android.resource://"
                    + getApplicationContext().getPackageName() + "/" + R.raw.siren));

        cat_str = data.get("cat_str");

        if (cat_str.equals("Fire"))
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.picflam)); // acquire an external resource by using URI(Uniform Resource Identifier)
        else if(cat_str.equals("Explosion"))
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.picexplo));
        else if(cat_str.equals("Chemical"))
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.picskull));
        else if(cat_str.equals("Bio"))
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.picsilho));
        else if(cat_str.equals("Corrosion"))
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.picacid));
        else
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_alert));

        return notificationBuilder;
    }

    private void sendNotification(Map<String, String> data, boolean near){

        String NOTIFICATION_SOUND = "notifcation with siren";
        String NOTIFICATION_SILENT = "notification without siren";

        Intent registerIntent = buildIntent(data);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent registerPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, registerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        /**
         * Notification Channel
         */
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel soundChannel = makeChannel(NOTIFICATION_SOUND, Uri.parse("android.resource://"
                    + getApplicationContext().getPackageName() + "/" + R.raw.siren));

            NotificationChannel silentChannel =  makeChannel(NOTIFICATION_SILENT, null);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(soundChannel);
            notificationManager.createNotificationChannel(silentChannel);
        }

        /**
         * Notification Compat
         */
        NotificationCompat.Builder soundBuilder = buildNotification(NOTIFICATION_SOUND, data,
                    registerPendingIntent, near);

        NotificationCompat.Builder silentBuilder = buildNotification(NOTIFICATION_SILENT, data,
                registerPendingIntent, near);

        assert notificationManager != null;

        if (near)
            notificationManager.notify(NOTIFICATION_ID /* ID of notification */, soundBuilder.build());
        else
            notificationManager.notify(NOTIFICATION_ID, silentBuilder.build());

        NOTIFICATION_ID++;
    }
}
