package com.example.cs408_app;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cs408_app.Model.CommentElement;

public class ReplyCommentActivity extends AppCompatActivity {

    private TextView textView;
    private CommentElement oParent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);
        Bundle args = getIntent().getExtras();
        oParent = (CommentElement) args.getSerializable("parent");
        LinearLayout parentLayout = findViewById(R.id.parent_layout);
        textView = parentLayout.findViewById(R.id.text_name);
        textView.setText(oParent.getAuthor().getName());
        textView = parentLayout.findViewById(R.id.text_contents);
        textView.setText(oParent.getContents());
    }
}
