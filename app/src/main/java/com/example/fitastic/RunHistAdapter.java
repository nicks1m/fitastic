package com.example.fitastic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RunHistAdapter extends FirebaseRecyclerAdapter<RunHistData, RunHistAdapter.myviewholder> {

    public RunHistAdapter(@NonNull FirebaseRecyclerOptions<RunHistData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull RunHistData RunHistData) {

        holder.distance.setText(String.valueOf(RunHistData.getDistance()));
        holder.duration.setText(String.valueOf(RunHistData.getDuration()));
        holder.speed.setText(String.valueOf(RunHistData.getSpeed()));


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView distance,duration,speed;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            distance=(TextView)itemView.findViewById(R.id.date_text);
            duration=(TextView)itemView.findViewById(R.id.distance_text);
            speed=(TextView)itemView.findViewById(R.id.pace_text);




        }
    }
}
