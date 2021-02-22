package com.example.fitastic;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class viewHolder extends RecyclerView.ViewHolder {

    TextView textName, textCal;

    public viewHolder(@NonNull View itemView) {
        super(itemView);

        textName = (TextView) itemView.findViewById(R.id.textViewName);
        textCal = (TextView) itemView.findViewById(R.id.textViewCal);
    }
}
