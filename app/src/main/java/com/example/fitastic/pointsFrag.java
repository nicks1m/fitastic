package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pointsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pointsFrag extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private NavController controller;
    private DatabaseReference ref;
    private String tierPoints;
    private String spendingPoints;
    private TextView current_pts;
    private ProgressBar prog_bar;
    private LinearLayout challenges_layout;
    private int nextTier;
    private ArrayList<Integer> tierLevels;
    private TextView points_to_next_tier;
    private TextView currentTier;
    private double percent = 0;
    private TextView qty_5k,
                     qty_10k,
                     qty_15k,
                     qty_21k,
                     qty_42k;
    private TextView challenge_title, challenge_points, unfinished;
    private Button rewards_btn;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public pointsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pointsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static pointsFrag newInstance(String param1, String param2) {
        pointsFrag fragment = new pointsFrag();
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


        View v = inflater.inflate(R.layout.fragment_points, container, false);

        controller = Navigation.findNavController(container);
        prog_bar = v.findViewById(R.id.progress_bar_points);
        current_pts = v.findViewById(R.id.current_points);
        points_to_next_tier = v.findViewById(R.id.textview_progress);
        currentTier = v.findViewById(R.id.label_tier);
        qty_5k = v.findViewById(R.id.qty_5k);
        qty_10k = v.findViewById(R.id.qty_10k);
        qty_15k = v.findViewById(R.id.qty_15k);
        qty_21k = v.findViewById(R.id.qty_21k);
        qty_42k = v.findViewById(R.id.qty_42k);
        challenges_layout = v.findViewById(R.id.challenges_layout);
        rewards_btn = v.findViewById(R.id.rewards_btn);
        rewards_btn.setOnClickListener(v1->{
            controller.navigate(R.id.action_pointsFrag_to_rewardsFrag);
        });

        createChallenges();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("points");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tierPoints = snapshot.child("tierpoints").getValue().toString();
                spendingPoints = snapshot.child("availpoints").getValue().toString();
                current_pts.setText(spendingPoints);
                //Initialize Awards
                qty_5k.setText(snapshot.child("awards").child("5k").getValue().toString());
                qty_10k.setText(snapshot.child("awards").child("10k").getValue().toString());
                qty_15k.setText(snapshot.child("awards").child("15k").getValue().toString());
                qty_21k.setText(snapshot.child("awards").child("21k").getValue().toString());
                qty_42k.setText(snapshot.child("awards").child("42k").getValue().toString());
                //Initialize Tiers
                initializeTiers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return v;
    }

    //Create Tier system using ArrayList index that represents the level, and value in each position represents the points required for next level.
    //eg [0] = 1000pts    [1] = 2000pts   [2] = 3000pts
    private void initializeTiers(){

        tierLevels = new ArrayList<>();
        for(int i = 0; i < 10; i ++){
            tierLevels.add((i+1)*1500);

        }

        double ctp = Integer.parseInt(tierPoints);
        for(int i = 0; i <tierLevels.size(); i ++){
            if(ctp < tierLevels.get(i)){
                nextTier = tierLevels.get(i);
                System.out.println("Next Tier is" +  (i+1) + ", Points required " + (nextTier - ctp));
                points_to_next_tier.setText(ctp + "/" + nextTier);
                currentTier.setText(String.valueOf(i));
                System.out.println("Cp: " + ctp + " nextTier: " + nextTier);
                percent = Math.floor((ctp/nextTier)*100);
                updateProgressBar(percent);
                return;
            }
        }
    }

    private void updateProgressBar(double percent){
        prog_bar.setProgress((int)percent);
    }

    private void createChallenges(){
        //objects of challenges stored in arraylist
        //iterate through arraylist and create dynanimcally
        challengeGenerator.generateChallenges();
        for(int i = 0; i < 4; i++){
            addChallenge(challengeGenerator.challenges.get(i).getChallenge(),challengeGenerator.challenges.get(i).getPoints());
        }

    }

    private void addChallenge(String title, int points){
        LinearLayout layout_box = new LinearLayout(getContext());
        challenge_title = new TextView(getContext());
        challenge_title.setText(title);
        challenge_title.setEms(10);
        challenge_title.setPadding(50,0,0,0);
        challenge_points = new TextView(getContext());
        challenge_points.setText(String.valueOf(points));
        challenge_points.setEms(5);
        challenge_points.setPadding(80,0,0,0);
        unfinished = new TextView(getContext());
        unfinished.setText("Unfinished");
        unfinished.setEms(8);
        unfinished.setPadding(80,0,0,0);

        layout_box.setBackgroundColor(getResources().getColor(R.color.challengeGrey));
        layout_box.setMinimumHeight(150);
        layout_box.setDividerPadding(50);
        layout_box.addView(challenge_title);
        layout_box.addView(challenge_points);
        layout_box.addView(unfinished);

        challenges_layout.addView(layout_box);

    }
}