package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link multiPlayServer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class multiPlayServer extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    private Button join;
    private EditText room_id;
    private DatabaseReference room_ref;
    private DatabaseReference player_ref;
    private ValueEventListener mListener;

    private String p_name;
    private TextView playerName;
    private TextView isReady;
    private boolean check = false;
    private String room_id_s;

    private Button btn_ready;

    private LinearLayout players;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public multiPlayServer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment multiPlayServer.
     */
    // TODO: Rename and change types and number of parameters
    public static multiPlayServer newInstance(String param1, String param2) {
        multiPlayServer fragment = new multiPlayServer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multi_play_server, container, false);
        btn_ready = v.findViewById(R.id.ready_btn);
        players = v.findViewById(R.id.playerlist);

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
                    Log.d("Reaad Fail", "Error");
                }
            });
        });

        btn_ready.setOnClickListener(v1->{
            check = !check;
            room_ref.child("players").child(auth.getCurrentUser().getUid()).child("isReady").setValue(check);
        });



        return v;
    }



    public void addPlayer(String name, String ready){
        LinearLayout layout_box = new LinearLayout(getActivity());

        playerName = new TextView(getActivity());
        playerName.setText(name);

        isReady = new TextView(getActivity());
        isReady.setText(ready);

        layout_box.setBackgroundColor(getResources().getColor(R.color.challengeGrey));
        layout_box.addView(playerName);
        layout_box.addView(isReady);
        players.addView(layout_box);


       }

    public void removePlayers(){
        players.removeAllViews();
    }

    @Override
    public void onDestroy() {
        //Remove event listener when frag is inactive to prevent async callbacks and unnecessary data changes
        super.onDestroy();
        player_ref.removeEventListener(mListener);
//        ready_ref.removeEventListener(rListener);
    }
//    public void onCheckboxClicked(View view) {
//        // Is the view now checked?
//        boolean checked = ((CheckBox) view).isChecked();
//
//        // Check which checkbox was clicked
//        switch(view.getId()) {
//            case R.id.p1_ready:
//                if (checked)
//
//                    System.out.println("p1 is ready");
//            else
//                    System.out.println("p1 not ready");
//                break;
//            case R.id.p2_ready:
//                if (checked)
//                    System.out.println("p2 is ready");
//            else
//                    System.out.println("p2 not ready");
//                break;
//
//        }
//    }
}