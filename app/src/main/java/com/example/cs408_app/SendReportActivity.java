package com.example.cs408_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.Alarm;
import com.example.cs408_app.Model.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendReportActivity extends AppCompatActivity {
    Button report_btn;
    EditText titleText;
    EditText descText;
    RadioGroup category;
    CS4CSApi apiService;
    Retrofit retrofit;


    SharedPreferences preferences;

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
                // if the response is successful
                if (response.isSuccessful() && response.body() != null && response.code() == 201) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                // if the response is not successful (then app receives intended error message)
                else if (!response.isSuccessful() && response.errorBody() != null) {
                    Converter<ResponseBody, Response> errorConverter =
                            retrofit.responseBodyConverter(Response.class, new Annotation[0]);

                    try {
                        Response error = errorConverter.convert(response.errorBody());
                        // show the error message
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Something wrong, please try again !", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Unknown error" , Toast.LENGTH_LONG).show();
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

        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively

        report_btn = findViewById(R.id.button_report);
        titleText = (EditText) findViewById(R.id.title_input);
        category = (RadioGroup) findViewById(R.id.category);
        descText = (EditText) findViewById(R.id.desc_input);

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
        apiService = retrofit.create(CS4CSApi.class);

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
                        String reporter = preferences.getString("user_email", "UNVERIFIED");
                        Alarm alarm = new Alarm(geo_lat, geo_lng, geo_rad, title, cat_str, desc, reporter);
                        postData(alarm);
                        finish();
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
