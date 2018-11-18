package com.example.cs408_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.cs408_app.Model.AlarmElement;

public class AlarmViewActivity extends AppCompatActivity {

    TextView textView;
    AlarmElement oAlarm;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);
        Bundle args = getIntent().getExtras();
        oAlarm = (AlarmElement) args.getSerializable("alarm");

        textView = findViewById(R.id.text_title);
        textView.setText(oAlarm.getTitle());

        textView = findViewById(R.id.text_desc);
        textView.setText(oAlarm.getDesc());

        textView = findViewById(R.id.text_date);
        textView.setText(oAlarm.getCreated_at());

        textView = findViewById(R.id.text_cat);
        textView.setText(oAlarm.getCat_str());

        preferences = getSharedPreferences("register", MODE_PRIVATE);
        Boolean is_official = preferences.getBoolean("is_official", false);
        if(is_official){

        }
        else{
            View v = (View)findViewById(R.id.include_reporter_info);
            v.setVisibility(View.GONE);
        }

    }
}
