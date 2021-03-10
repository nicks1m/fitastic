package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link multiPlayServer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class multiPlayServer extends Fragment {

    // debug
    private static String TAG = "multiPlayServer";

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    private NavController controller;

    private Button join;
    private EditText room_id;
    private DatabaseReference room_ref;
    private DatabaseReference player_ref;
    private ValueEventListener mListener;

    private String p_name;
    private TextView playerName;
    private TextView isReady;
    private boolean check = false;
    private boolean isStart = false;
    private String room_id_s;

    private Button btn_ready;
    private Button btn_start;

    private LinearLayout players;

    public multiPlayServer() {
        // Required empty public constructor
    }

    public static multiPlayServer newInstance() {
        multiPlayServer fragment = new multiPlayServer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multi_play_server, container, false);

        btn_ready = v.findViewById(R.id.ready_btn);
        players = v.findViewById(R.id.playerlist);
        btn_start = v.findViewById(R.id.start_btn);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference name_ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("display name");
        name_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                p_name = String.valueOf(snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        room_id = v.findViewById(R.id.data_room_id);

        join = v.findViewById(R.id.join_room);
        join.setOnClickListener(v1->{
            room_ref = mDatabase.child("Rooms").child(room_id.getText().toString());
            room_id_s = room_id.getText().toString();

               room_ref.child("players").child(auth.getCurrentUser().getUid()).child("isReady").setValue(false);
               room_ref.child("players").child(auth.getCurrentUser().getUid()).child("name").setValue(p_name);
               btn_ready.setVisibility(View.VISIBLE);
               btn_start.setVisibility(View.VISIBLE);
               join.setVisibility(View.GONE);
               room_id.setInputType(0);

            player_ref = mDatabase.child("Rooms").child(room_id_s).child("players");
            mListener = player_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    removePlayers();
                    for(DataSnapshot player_snapshot : dataSnapshot.getChildren()){
                        addPlayer(String.valueOf(player_snapshot.child("name").getValue()),String.valueOf(player_snapshot.child("isReady").getValue()));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Read Fail", "Error");
                }
            });
        });

        btn_ready.setOnClickListener(v1->{
            check = !check;
            room_ref.child("players").child(auth.getCurrentUser().getUid()).child("isReady").setValue(check);
        });

        btn_start.setOnClickListener(v2 -> {
            DatabaseReference reference = mDatabase.child("Rooms")
                    .child(room_id_s)
                    .child("players");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    removePlayers();
                    for(DataSnapshot player_snapshot : dataSnapshot.getChildren()){
                        if (isAllReady(dataSnapshot)) {
                            // handle how start will function with multiplayer here
                            controller.navigate(R.id.action_multiPlayServer_to_startFrag);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Read Fail", "Error");
                }
            });
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }

    public void addPlayer(String name, String ready){
        LinearLayout layout_box = new LinearLayout(getActivity());
        layout_box.setMinimumHeight(50);
        layout_box.setPadding(0,0,0,20);

        playerName = new TextView(getActivity());
        playerName.setText(name);
        isReady = new TextView(getActivity());
        isReady.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        isReady.setGravity(Gravity.RIGHT);
        isReady.setPadding(0,0,30,0);


        if (ready.equals("true")){
            isReady.setText("Ready");
        } else {
            isReady.setText("Not Ready");
        }

        layout_box.setBackgroundColor(getResources().getColor(R.color.cardGrey));
        layout_box.addView(playerName);
        layout_box.addView(isReady);
        players.addView(layout_box);
       }

    public void removePlayers(){
        players.removeAllViews();
    }

    private boolean isAllReady(DataSnapshot snapshot) {
        for (DataSnapshot sp : snapshot.getChildren()) {
            if ( !((boolean) (sp.child("isReady").getValue()) )) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        //Remove event listener when frag is inactive to prevent async callbacks and unnecessary data changes
        super.onDestroy();
        player_ref.removeEventListener(mListener);

    }

}