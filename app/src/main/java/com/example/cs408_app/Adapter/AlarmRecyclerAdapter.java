package com.example.cs408_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs408_app.AlarmViewActivity;
import com.example.cs408_app.AllAlarmsActivity;
import com.example.cs408_app.MainActivity;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.R;

import java.util.List;

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.ViewHolder>{

    private List<AlarmElement> item;
    Context context;

    public AlarmRecyclerAdapter(Context context, List<AlarmElement> item){
        this.item = item;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_alarm_row, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(item.get(position).getTitle());
        holder.desc.setText(item.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, desc;
        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_title);
            desc = (TextView) itemView.findViewById(R.id.text_desc);
        }


    }
}
