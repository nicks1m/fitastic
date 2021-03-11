package com.example.fitastic;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitastic.utility.RunDbUtility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RunHistAdapter extends FirebaseRecyclerAdapter<RunHistData, RunHistAdapter.myviewholder> {

    public RunHistAdapter(@NonNull FirebaseRecyclerOptions<RunHistData> options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull RunHistData RunHistData) {
        Bitmap bitmap = RunDbUtility.stringToBitmap(RunHistData.getBitmap());
        holder.image.setImageBitmap(Bitmap.createScaledBitmap(bitmap,500,250,false));
//        String dformat = String.valueOf(RunDbUtility.convertEpochToDate(Long.parseLong(RunHistData.getDate())));
//        holder.date.setText((dformat.replaceAll("GMT","")));
        holder.distance.setText(RunDbUtility.calculateDistance(String.valueOf(RunHistData.getDistance())));
        holder.duration.setText(RunDbUtility.calculateDistance(String.valueOf(RunHistData.getDuration())));
        holder.speed.setText(RunDbUtility.calculatePace(String.valueOf(RunHistData.getDistance()),String.valueOf(RunHistData.getDuration())));


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView distance,duration,speed,date;
        ImageView image;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.run_route);
            date = itemView.findViewById(R.id.run_date);
            distance=(TextView)itemView.findViewById(R.id.distance_text);
            duration=(TextView)itemView.findViewById(R.id.duration_text);
            speed=(TextView)itemView.findViewById(R.id.speed_text);




        }
    }
}
