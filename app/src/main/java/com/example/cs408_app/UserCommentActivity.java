package com.example.cs408_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.Model.Comment;
import com.example.cs408_app.Model.Response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserCommentActivity extends AppCompatActivity {

    AlarmElement oAlarm;
    Retrofit retrofit;
    CS4CSApi apiService;
    TextView textView;
    EditText editText;
    Button button;
    SharedPreferences preferences;

    final String TAG = "UserCommentActivity";

    public void makeComment(Comment comment){
        Call<Response> call = apiService.makeComment(comment, oAlarm.get_id());
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Toast.makeText(getApplicationContext(), "Made comment", Toast.LENGTH_LONG).show();
                //TODO: refresh view
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Bundle args = getIntent().getExtras();
        oAlarm = (AlarmElement) args.getSerializable("alarm");

        textView = findViewById(R.id.text_title);
        textView.setText(oAlarm.getTitle());

        editText = findViewById(R.id.text_comment);
        button = findViewById(R.id.button_comment);

        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
        apiService = retrofit.create(CS4CSApi.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();
                editText.setText(""); // clear comment edit text
                String author = preferences.getString("user_email", "UNVERIFIED");
                makeComment(new Comment(author, content));
            }
        });
    }

}
