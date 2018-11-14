package com.example.cs408_app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.MyFirebaseMessagingService.*;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Shared Preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button;

        // Retrieve and hold the contents of the preferences file "register"
        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively

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
                dataMap.put("content", "Why is the mathematics is the language of science??");
                sendNotification(dataMap);
            }
        });

        Boolean is_registered = preferences.getBoolean("is_registered",false);
        if(!is_registered){
            Toast.makeText(this, "Not registered!", Toast.LENGTH_SHORT).show();
            if(Constants.cheat_login){
                preferences.edit().putString("user_email", Constants.cheat_email).commit();
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
    private void sendNotification(Map<String, String> dataMap){

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "MY_channel")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(dataMap.get("title"))
                .setContentText(dataMap.get("content"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
