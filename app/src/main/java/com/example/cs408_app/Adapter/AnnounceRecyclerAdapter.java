package com.example.cs408_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cs408_app.Model.AnnounceElement;
import com.example.cs408_app.Model.CommentElement;
import com.example.cs408_app.R;
import com.example.cs408_app.ReplyCommentActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AnnounceRecyclerAdapter extends RecyclerView.Adapter<AnnounceRecyclerAdapter.ViewHolder>{

    private List<AnnounceElement> item;
    public String alarm_id;
    Context context;

    public AnnounceRecyclerAdapter(Context context, List<AnnounceElement> item, String alarm_id){
        this.item = item;
        this.context = context;
        this.alarm_id = alarm_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_announce_row, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AnnounceElement oAnnounce = item.get(position);

        final String OLD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        final String NEW_FORMAT = "MM월 dd일 HH:mm";

        String oldDateString = oAnnounce.getCreated_at();
        String newDateString;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);

        holder.date.setText(newDateString);
        holder.contents.setText(oAnnounce.getContents());
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView date, contents;
        public ViewHolder(View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.text_date);
            contents = itemView.findViewById(R.id.text_contents);
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
