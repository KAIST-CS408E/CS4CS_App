package com.example.cs408_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cs408_app.AlarmViewActivity;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.Model.Comment;
import com.example.cs408_app.Model.CommentElement;
import com.example.cs408_app.R;
import com.example.cs408_app.ReplyCommentActivity;
import com.example.cs408_app.UserCommentActivity;

import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>{

    private List<CommentElement> item;
    public String alarm_id;
    Context context;

    public CommentRecyclerAdapter(Context context, List<CommentElement> item, String alarm_id){
        this.item = item;
        this.context = context;
        this.alarm_id = alarm_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_comment_row, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentElement oComment = item.get(position);
        holder.name.setText(oComment.getAuthor().getName());
        holder.contents.setText(oComment.getContents());
        String suffix = holder.replies.getText().toString();
        holder.replies.setText(String.valueOf(oComment.getNum_replies())+suffix);
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, contents, replies;
        public ViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            contents = itemView.findViewById(R.id.text_contents);
            replies = itemView.findViewById(R.id.text_replies);
            replies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommentElement selected = item.get(position);
                        // do something
                        Intent intent = new Intent(context, ReplyCommentActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("parent", selected);
                        args.putString("alarm_id", alarm_id);
                        intent.putExtras(args);
                        context.startActivity(intent);
                    }
                }
            });
            /*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommentElement selected = item.get(position);
                        // do something
                    }
                }
            });
            */
        }


    }
}
