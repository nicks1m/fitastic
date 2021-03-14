package com.example.fitastic;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link rewardsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class rewardsFrag extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private LinearLayout avail_rewards;
    private ArrayList<Integer> tierLevels;
    private LinearLayout claim_history;
    private LinearLayout reward_box;
    private LinearLayout claim_box;
    private TextView brand;
    private TextView type;
    private TextView discount;
    private TextView points;
    private Button claim;
    private Button view_code;
    private String spendingPoints;
    private TextView spending_pts;
    private TextView tierLevel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public rewardsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment rewardsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static rewardsFrag newInstance(String param1, String param2) {
        rewardsFrag fragment = new rewardsFrag();
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


        View v = inflater.inflate(R.layout.fragment_rewards, container, false);
        avail_rewards = v.findViewById(R.id.available_rewards);
        spending_pts = v.findViewById(R.id.spending_pts);
        tierLevel = v.findViewById(R.id.tier_level);
        claim_history = v.findViewById(R.id.claim_history);


        initializeTiers();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("points");

        //sumamry : generate, Push data to firebase, check if the amount of rewards is less < 5. if true, push new reward to fill missing slots

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Remove views if not duplicates will be seen
                removeViews();

                //Initialize some data, e.g tier level and points
                int tierPoints = Integer.parseInt(snapshot.child("tierpoints").getValue().toString());
                spendingPoints = snapshot.child("availpoints").getValue().toString();
                spending_pts.setText(spendingPoints);
                tierLevel.setText("Tier " + calculateTier(tierPoints) + " Rewards");

                //if rewards < 5 in list, update with new rewards
                if(snapshot.child("unclaimed").getChildrenCount() < 5){
                    Object a = new rewardsGenerator(calculateTier(tierPoints));
                    ref.child("unclaimed").push().setValue(a);
                }
                //generate views
                for(DataSnapshot r : snapshot.child("unclaimed").getChildren()){
                    addRewards(r.getKey(),
                            r.child("brands").getValue().toString(),
                            r.child("type").getValue().toString(),
                            r.child("discount").getValue().toString(),
                            r.child("points").getValue().toString());
                }

                for(DataSnapshot r : snapshot.child("claimed").getChildren()){
                    addClaimedRewards(r.getKey(),
                            r.child("brands").getValue().toString(),
                            r.child("type").getValue().toString(),
                            r.child("discount").getValue().toString(),
                            r.child("points").getValue().toString());

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return v;
    }
    private void addClaimedRewards(String id, String brand_name, String type_item, String discount_amt, String points_req) {

        String key = id;

        //Initialize various textviews and set parameters
        claim_box = new LinearLayout(getContext());
        claim_box.setMinimumHeight(100);
        claim_box.setBackgroundColor(getResources().getColor(R.color.cardGrey));

        brand = new TextView(getContext());
        brand.setText(brand_name);
        brand.setPadding(50,0,0,0);
        brand.setEms(8);

        type = new TextView(getContext());
        type.setText(type_item);
        type.setEms(5);


        discount = new TextView(getContext());
        discount.setText(discount_amt + "%");
        discount.setEms(3);


        points = new TextView(getContext());
        points.setText(points_req);
        points.setEms(4);


        view_code = new Button(getContext());
        view_code.setText("View");
        view_code.setBackgroundColor(getResources().getColor(R.color.pageBG));
        view_code.setOnClickListener(v->{

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Redemption Code");
            builder.setMessage("Your discount code is " + brand_name.substring(0,3).toUpperCase() + discount_amt + points_req);

            // add a button
            builder.setPositiveButton("OK", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        claim_box.addView(brand);
        claim_box.addView(type);
        claim_box.addView(discount);
        claim_box.addView(points);
        claim_box.addView(view_code);


        claim_history.addView(claim_box);
    }

    private void addRewards(String id, String brand_name, String type_item, String discount_amt, String points_req){

        //key to be stored for reward removal later on
        String key = id;

        //Initialize various textviews and set parameters
        reward_box = new LinearLayout(getContext());
        reward_box.setMinimumHeight(100);

        brand = new TextView(getContext());
        brand.setText(brand_name);
        brand.setPadding(50,0,0,0);
        brand.setEms(8);

        type = new TextView(getContext());
        type.setText(type_item);
        type.setEms(5);


        discount = new TextView(getContext());
        discount.setText(discount_amt + "%");
        discount.setEms(3);


        points = new TextView(getContext());
        points.setText(points_req);
        points.setEms(3);


        claim = new Button(getContext());
        claim.setText("claim");
        claim.setTextSize(12);
        claim.setBackgroundColor(getResources().getColor(R.color.pageBG));
        claim.setOnClickListener(v->{
            DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("points");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //calculate new amt of points
                    if(Integer.parseInt(snapshot.child("availpoints").getValue().toString()) < Integer.parseInt(points_req)){
                        Toast.makeText(getContext(), "Not enough Points", Toast.LENGTH_SHORT).show();
//                        return;

                    } else {
                        //Prevent balance points from hitting negative.
                        int newPoints = Integer.parseInt(snapshot.child("availpoints").getValue().toString()) - Integer.parseInt(points_req);
                        if(newPoints < 0){
                            newPoints = 0;
                        }
                        //Set post-deduction balance points
                        ref.child("availpoints").setValue(String.valueOf(newPoints));

                        //create hashmap to store details
                        HashMap<String, Object> reward = new HashMap<>();
                        reward.put("brands", brand_name);
                        reward.put("type", type_item);
                        reward.put("discount", discount_amt);
                        reward.put("points", points_req);

                        //move data to claimed history node
                        ref.child("claimed").push().setValue(reward);

                        //Remove from unclaimed view, and db
                        reward_box.setVisibility(View.GONE);
                        ref.child("unclaimed").child(key).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        //Add elements to views
        reward_box.addView(brand);
        reward_box.addView(type);
        reward_box.addView(discount);
        reward_box.addView(points);
        reward_box.addView(claim);

        avail_rewards.addView(reward_box);

    }

    private void initializeTiers(){

        tierLevels = new ArrayList<>();
        for(int i = 0; i < 10; i ++){
            tierLevels.add((i+1)*1500);

        }
    }

    private int calculateTier(int points){
        //calculate current tier
        int tier = 0;
        for(int i = 0; i <tierLevels.size(); i ++){
            if(points < tierLevels.get(i)) {
                System.out.println("current tier: " + i);
                tier = i;
                return tier;
            }

            }
        return tier;
    }


    private void removeViews(){
        avail_rewards.removeAllViews();
        claim_history.removeAllViews();
    }
    }