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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "FirebaseService";

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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            sendNotification(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(RemoteMessage remoteMessage){

        final int NOTIFICATION_ID = 1;
        final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent registerIntent = new Intent(this, MainActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent registerPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, registerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Map<String, String> data = remoteMessage.getData();

        /**
         * Notification Channel
         */
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{500, 5000, 500, 5000, 500, 5000});
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        /**
         * Notification Compat
         */
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                // Show notification even on lock screen
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                // use custom mp3 sound file (./res/raw/siren.mp3)
                .setSound(Uri.parse("android.resource://"
                        + getApplicationContext().getPackageName() + "/" + R.raw.siren))
                // off-vibrate time(ms), on-vibrate time, off time, on time, off, on, ...
                .setVibrate(new long[] {500, 3000, 500, 3000})
                // Icon size
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_alert)) // acquire an external resource by using URI(Uniform Resource Identifier)
                .setSmallIcon(R.mipmap.ic_launcher) // using mipmap, produce smaller icon
                // Contents
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                // When a user expands(slide down) original sized notification(above contents), show more info
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("So I say to you: Ask and it will be given to you; seek and you will find; knock and the door will be opened to you. _Luke11:9"))
                // If user click the notification,
                .setContentIntent(registerPendingIntent)
                .setAutoCancel(true)
                // If user click a button after expanding the notification
                .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark, "Register", registerPendingIntent))
                .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark, "Enter", registerPendingIntent));

        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
    }
}
