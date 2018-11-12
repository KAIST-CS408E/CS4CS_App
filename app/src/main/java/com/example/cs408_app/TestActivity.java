package com.example.cs408_app;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Model.Response;
import com.example.cs408_app.Config.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestActivity extends Activity {

    private Retrofit retrofit;
    private TextView textView;
    private CS4CSApi cs4csApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection_test);
        textView = findViewById(R.id.welcome_text);


        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.server_ip + Constants.server_port)
                .addConverterFactory(GsonConverterFactory.create()) // convert request inputs into JSON, and recognize response outputs as JSON.
                .build();

        // indicate the interface we are going to use
        cs4csApi = retrofit.create(CS4CSApi.class);

        displayWelcomeMsg();
    }

    public void displayWelcomeMsg() {

        Call<Response> call = cs4csApi.welcomeMsg();

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String msg = response.body().getMessage();
                    textView.setText(msg);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("Server Connection Fail", t.getLocalizedMessage());
                Toast.makeText(TestActivity.this, "Server Connection Fail", Toast.LENGTH_LONG).show();
            }
        });
    }
}
