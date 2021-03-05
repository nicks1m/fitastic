package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ShowDataActivity extends AppCompatActivity {
    private RecyclerView recview;
    private FirebaseAuth auth;
    RunHistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);


        auth = FirebaseAuth.getInstance();

        recview= (RecyclerView)findViewById(R.id.recyclerview);
        recview.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<RunHistData> options =
                new FirebaseRecyclerOptions.Builder<RunHistData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Runs"), RunHistData.class)
                        .build();

        adapter = new RunHistAdapter(options);
        recview.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}