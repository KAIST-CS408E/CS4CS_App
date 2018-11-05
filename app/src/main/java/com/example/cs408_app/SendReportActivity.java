package com.example.cs408_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cs408_app.Model.Alarm;
import com.example.cs408_app.Model.Response;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendReportActivity extends AppCompatActivity {
    Button report_btn;
    EditText titleText;
    EditText descText;
    RadioGroup category;
    ApiService apiService;
    Retrofit retrofit;

    // geofencing parameters
    double geo_lat;
    double geo_lng;
    double geo_rad;

    final String TAG = "SendReportActivity";

    public void postData(Alarm alarm){
        Call<Response> call = apiService.postAlarm(alarm);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);

        report_btn = findViewById(R.id.button_report);
        titleText = (EditText) findViewById(R.id.title_input);
        category = (RadioGroup) findViewById(R.id.category);
        descText = (EditText) findViewById(R.id.desc_input);

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(ApiService.API_URL).build();
        apiService = retrofit.create(ApiService.class);

        Intent intent = getIntent();
        geo_lat = intent.getDoubleExtra("Latitude", 0);
        geo_lng = intent.getDoubleExtra("Longitude", 0);
        geo_rad = intent.getDoubleExtra("Radius", 0);


        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(SendReportActivity.this);
                alert.setTitle("Report");
                alert.setMessage("Are you sure?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String title = titleText.getText().toString();
                        int cat_id = category.getCheckedRadioButtonId();
                        RadioButton r = (RadioButton) category.findViewById(cat_id);
                        String cat_str = r.getText().toString();
                        String desc = descText.getText().toString();

                        // send alert
                        Alarm alarm = new Alarm(geo_lat, geo_lng, geo_rad, title, cat_str, desc);
                        postData(alarm);

                        Toast.makeText(SendReportActivity.this, "title: "+title+"\ncategory: "+cat_str+"\n", Toast.LENGTH_SHORT).show();

                        dialogInterface.dismiss();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }
        });
    }
}
