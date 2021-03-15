package com.example.fitastic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.fitastic.repositories.MainRepository;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowDataActivity extends AppCompatActivity {
    private RecyclerView recview;
    private FirebaseAuth auth;
    RunHistAdapter adapter;
    private ArrayList<String> epochTimes = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        auth = FirebaseAuth.getInstance();

        recview= findViewById(R.id.recyclerview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        // gets dist, speed, bitmap, duration
        FirebaseRecyclerOptions<RunHistData> options =
                new FirebaseRecyclerOptions.Builder<RunHistData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Runs"), RunHistData.class)
                        .build();

        // bug im getting is unless an adapter is created using the options then options.getSnapshots
        // doesn't get the stats (line 62), But when a new adapter is created it automatically
        // calls the onBindViewHolder before i can update the options with the epochs
        // so if you create adapter all run info is displayed before u can get the date, but if you
        // don't create one then you cant get the options.getSnapshots line 62
        // adapter = new RunHistAdapter(options);


        // gets all epoch times from firebase, used to get date
        MainRepository.epochTimes.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                // save epoch times
                epochTimes = strings;
                // append corresponding epoch time to runHistDate
                for (int i = 0; i < options.getSnapshots().size(); i++) {
                    options.getSnapshots().get(i).setDate(Long.valueOf(epochTimes.get(i)));
                }
                // display final options to runs page
                displayRuns(options);
            }
        });
        // updates epoch times for MainRepository, previous observer will execute when change d
        // detected
        MainRepository.updateRunEpochTimes();
    }

    private void displayRuns(FirebaseRecyclerOptions<RunHistData> options) {
        // create adapter with final options
        if (adapter == null)
            adapter = new RunHistAdapter(options);
        recview.setAdapter(adapter);
        //adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // originally condition wasn't here but ive changed where the adapter is made
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}