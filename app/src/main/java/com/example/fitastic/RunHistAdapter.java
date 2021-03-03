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
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull RunHistData model) {

//        holder.date.setText(RunHistData.getDateHist());
//        holder.distance.setText(RunHistData.getDistanceHist());
//        holder.pace.setText(RunHistData.getPaceHist());
//        holder.time.setText(RunHistData.getTimeHist());
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView date,distance,pace,time;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            date=(TextView)itemView.findViewById(R.id.date_text);
            distance=(TextView)itemView.findViewById(R.id.distance_text);
            pace=(TextView)itemView.findViewById(R.id.pace_text);
            time=(TextView)itemView.findViewById(R.id.time_text);



        }
    }
}
