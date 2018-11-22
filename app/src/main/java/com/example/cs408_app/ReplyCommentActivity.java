package com.example.cs408_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs408_app.API.CS4CSApi;
import com.example.cs408_app.Adapter.CommentRecyclerAdapter;
import com.example.cs408_app.Adapter.ReplyRecyclerAdapter;
import com.example.cs408_app.Config.Constants;
import com.example.cs408_app.Model.Comment;
import com.example.cs408_app.Model.CommentElement;
import com.example.cs408_app.Model.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReplyCommentActivity extends AppCompatActivity {

    TextView textView;
    CommentElement oParent;
    Retrofit retrofit;
    CS4CSApi apiService;
    EditText editText;
    Button button;
    SharedPreferences preferences;

    List<CommentElement> commentList = null;
    private RecyclerView recycler;
    ReplyRecyclerAdapter adapter;


    final String TAG = "ReplyCommentActivity";

    public void makeReply(Comment comment, final String alarm_id){
        Call<Response> call = apiService.makeReply(comment, alarm_id, oParent.get_id());
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<com.example.cs408_app.Model.Response> call, retrofit2.Response<com.example.cs408_app.Model.Response> response) {
                Toast.makeText(getApplicationContext(), "Made comment", Toast.LENGTH_LONG).show();
                getReplyList(alarm_id); //refresh after sending
            }

            @Override
            public void onFailure(Call<com.example.cs408_app.Model.Response> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);
        Bundle args = getIntent().getExtras();
        oParent = (CommentElement) args.getSerializable("parent");
        final String alarm_id = args.getString("alarm_id");

        preferences = getSharedPreferences("register", MODE_PRIVATE); // can be edited by this app exclusively

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
        apiService = retrofit.create(CS4CSApi.class);

        // display parent comment
        LinearLayout parentLayout = findViewById(R.id.parent_layout);
        textView = parentLayout.findViewById(R.id.text_name);
        textView.setText(oParent.getAuthor().getName());
        textView = parentLayout.findViewById(R.id.text_contents);
        textView.setText(oParent.getContents());

        editText = findViewById(R.id.text_reply);
        button = findViewById(R.id.button_reply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contents = editText.getText().toString();
                editText.setText(""); // clear comment edit text
                String author = preferences.getString("user_email", "UNVERIFIED");
                makeReply(new Comment(author, contents), alarm_id);
            }
        });
    }

    private void getReplyList(String alarm_id){
        recycler = findViewById(R.id.recycler);
        commentList = new ArrayList<>();
        adapter = new ReplyRecyclerAdapter(ReplyCommentActivity.this, commentList);
        adapter.alarm_id = alarm_id;
        LinearLayoutManager layoutManager = new LinearLayoutManager(ReplyCommentActivity.this);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Get comment list from the server
        try{
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.server_ip + Constants.server_port).build();
            apiService = retrofit.create(CS4CSApi.class);
            Call<List<CommentElement>> call = apiService.getReplyList(alarm_id, oParent.get_id());
            call.enqueue(new Callback<List<CommentElement>>() {
                @Override
                public void onResponse(Call<List<CommentElement>> call, retrofit2.Response<List<CommentElement>> response) {
                    commentList = response.body();
                    Collections.sort(commentList, new sortByCreatedAt());
                    recycler.setAdapter(new ReplyRecyclerAdapter(getApplicationContext(), commentList));
                    recycler.smoothScrollToPosition(0);
                    /*
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    */
                }

                @Override
                public void onFailure(Call<List<CommentElement>> call, Throwable t) {
                    Log.d("AllAlarms","failed to get alarm list");
                    Toast.makeText(ReplyCommentActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e){
            Log.d("AllAlarms", "failed to get alarm list");
        }

    }

    class sortByCreatedAt implements Comparator<CommentElement> {
        @Override
        public int compare(CommentElement a, CommentElement b) {
            Date a_date, b_date;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.KOREA);

            try {
                a_date = format.parse(a.getCreated_at());
                b_date = format.parse(b.getCreated_at());
                return (a_date.getTime() > b_date.getTime()) ? 1 : (a_date.getTime() < b_date.getTime()) ? -1 : 0;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }

        }
    }
}
