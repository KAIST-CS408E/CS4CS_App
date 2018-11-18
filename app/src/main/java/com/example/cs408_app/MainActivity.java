package com.example.cs408_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.R.drawable;

import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.MyFirebaseMessagingService.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Shared Preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String TAG = "MainActiviy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button;
        Switch officialSw;

        // Retrieve and hold the contents of the preferences file "register"
        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively

        button =findViewById(R.id.alarm);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){

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
        button.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v){
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("alarm")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "unSubscribeSuccess";
                                    if(!task.isSuccessful())
                                        msg = "unSubscribeFail";

                                    Log.d(TAG, msg);
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
            }
        });

        button = findViewById(R.id.button_map);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.button_report);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.button_server_connection_test);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.button_register);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.button_all_alarms);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllAlarmsActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Test code for push alarm design
         */
        button = findViewById(R.id.button_test_self_alarm);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("title", "Math is everywhere!!");
                dataMap.put("content", "So I say to you");
                sendNotification(dataMap);
            }
        });

        Boolean is_registered = preferences.getBoolean("is_registered",false);
        if(!is_registered){
            Toast.makeText(this, "Not registered!", Toast.LENGTH_SHORT).show();
            if(Constants.cheat_login){
                preferences.edit().putString("user_email", Constants.cheat_email).apply();
            }
            else{
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                // remove the activity stack ("go back" button will close the app, not return to main activity)
                finish();
                startActivity(intent);
            }
        }
        else{
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
        tv.setText("Current user: "+ user_email);
    }

    /**
     * Test code for push alarm design
     */

    /**
     * setFullScreenIntent, registerIntent
     *
     * : launch the registerIntent(for example) instead of posting the notification to the status bar.
     * Only for use with extremely high-priority notifications demanding the user's immediate attention,
     * such as an incoming phone call or alarm clock that the user has explicitly set to a particular time.
     */
    private void sendNotification(Map<String, String> dataMap){

        /**
         * Notification Channel
         */
        final int NOTIFICATION_ID = 1;
        final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{500, 5000, 500, 5000, 500, 5000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent registerPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, registerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                // Show notification even on lock screen
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                // use custom mp3 sound file (./res/raw/siren.mp3)
                .setSound(Uri.parse("android.resource://"
                        + getApplicationContext().getPackageName() + "/" + R.raw.siren))
                // off-vibrate time(ms), on-vibrate time, off time, on time, off, on, ...
                .setVibrate(new long[] {500, 3000, 500, 3000})
                // Icon size
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), drawable.ic_dialog_alert)) // acquire an external resource by using URI(Uniform Resource Identifier)
                .setSmallIcon(R.mipmap.ic_launcher) // using mipmap, produce smaller icon
                // Contents
                .setContentTitle(dataMap.get("title"))
                .setContentText(dataMap.get("content"))
                // When a user expands(slide down) original sized notification(above contents), show more info
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText("So I say to you: Ask and it will be given to you; seek and you will find; knock and the door will be opened to you. _Luke11:9"))
                // If user click the notification,
                .setContentIntent(registerPendingIntent)
                .setAutoCancel(true)
                // If user click a button after expanding the notification
                .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark, "Register", registerPendingIntent))
                .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark, "Enter", registerPendingIntent));

        // notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
    }
}
