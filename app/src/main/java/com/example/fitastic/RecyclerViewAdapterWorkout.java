package com.example.fitastic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitastic.diet.Recipe;
import com.example.fitastic.diet.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterWorkout extends RecyclerView.Adapter<RecyclerViewAdapterWorkout.MyViewHolder> {

    private Context mContext ;
    private ArrayList<String> mData ;

    public RecyclerViewAdapterWorkout(Context mContext, ArrayList<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_workout,parent,false);
        return new MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(mData.get(position));
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("test");
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView cardView;
        Button btn;

        public MyViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.workout_title);
            cardView = itemView.findViewById(R.id.workout_cardview);
            btn = itemView.findViewById(R.id.btn_start_workout1);
        }
    }
}
