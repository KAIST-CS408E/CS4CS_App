package com.example.cs408_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AlarmViewActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);
        Bundle args = getIntent().getExtras();
        textView = (TextView)findViewById(R.id.text_title);
        textView.setText(args.getString("title"));
    }
}
