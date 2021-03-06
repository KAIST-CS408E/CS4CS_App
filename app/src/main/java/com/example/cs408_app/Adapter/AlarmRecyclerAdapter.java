package com.example.cs408_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs408_app.AlarmViewActivity;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.R;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.ViewHolder>{

    private List<AlarmElement> item;
    Context context;

    /**
     * Recycler view's images
     */
    private HashMap<String, Integer> pictograms = new HashMap<>();

    public AlarmRecyclerAdapter(Context context, List<AlarmElement> item){
        this.item = item;
        this.context = context;
        this.pictograms.put("Fire", R.drawable.picflam);
        this.pictograms.put("Explosion", R.drawable.picexplo);
        this.pictograms.put("Chemical", R.drawable.picskull);
        this.pictograms.put("Bio", R.drawable.picsilho);
        this.pictograms.put("Corrosion", R.drawable.picacid);
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
        if (pictograms.get(item.get(position).getCat_str()) != null)
            holder.pictogram.setImageResource(pictograms.get(item.get(position).getCat_str()));
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, desc;
        public ImageView pictogram;
        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.text_name);
            desc = itemView.findViewById(R.id.text_desc);
            pictogram = itemView.findViewById(R.id.pictogram);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        AlarmElement selected = item.get(position);

                        Intent intent = new Intent(context, AlarmViewActivity.class);

                        Bundle args = new Bundle();
                        args.putSerializable("alarm", selected);
                        intent.putExtras(args);
                        context.startActivity(intent);
                    }
                }
            });
        }


    }
}
