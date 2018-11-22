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
    Context context;

    public CommentRecyclerAdapter(Context context, List<CommentElement> item){
        this.item = item;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_comment_row, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(item.get(position).getAuthor().getName());
        holder.contents.setText(item.get(position).getContents());
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
