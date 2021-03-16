package com.example.fitastic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitastic.R;

import com.example.fitastic.diet.Recipe;
import com.example.fitastic.diet.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterWorkout extends RecyclerView.Adapter<RecyclerViewAdapterWorkout.MyViewHolder> {

    private Context mContext ;
    private ArrayList<Workout> mData ;
    private NavController controller;

    public RecyclerViewAdapterWorkout(Context mContext, ArrayList<Workout> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_workout,parent,false);
        controller = Navigation.findNavController(parent);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String[] text = {mData.get(position).getName()};
        holder.title.setText(text[0]);
        holder.btn.setText("Start");

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sl = "/";
                String p1;
                String p2;
                String p3;
                if(holder.sets.getText().equals("sets")) {
                    p1 = sl;
                } else {
                    p1 = holder.sets.getText().toString() + "/";
                }
                if(holder.reps.getText().equals("reps")) {
                    p2 = sl;
                } else {
                    p2 = holder.reps.getText().toString() + "/";
                }
                if(holder.kg.getText().equals("kg")) {
                    p3 = sl;
                } else {
                    p3 = holder.kg.getText().toString() + "/";
                }

                text[0] = text[0].concat(sl + p1 + p2 + p3);

                System.out.println(text[0]);

                Bundle args = new Bundle();
                args.putString("custom_workout_title", text[0]);
                controller.navigate(R.id.action_workoutSearchFrag_to_createCustomWorkout, args);
                System.out.println("test");
            }
        });


//        exercise_kg = new EditText(getContext());
//        exercise_kg.setInputType(2);
//        exercise_kg.setText(kg);
//        exercise_kg.setEms(3);
//        exercise_kg.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView cardView;
        EditText sets;
        EditText reps;
        EditText kg;
        Button btn;

        public MyViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.workout_title);
            cardView = itemView.findViewById(R.id.workout_cardview);
            btn = itemView.findViewById(R.id.btn_start_workout1);
            sets = itemView.findViewById(R.id.workout_sets);
            reps = itemView.findViewById(R.id.workout_reps);
            kg = itemView.findViewById(R.id.workout_kg);
        }
    }
}
