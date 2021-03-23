package com.example.fitastic.repositories;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.fitastic.models.Run;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;

public class MainRepository {

    /* MainRepository is designed to facilitate data communication between the app and firebase
    *  this involves the input and output of data. However this class is mainly used to complete
    *  operations needed from StartFrag and RunSummary.
    *
    *  All methods are static so they can be accessed from any class without an instance required.
    *  Using this class saves having to reinitialise firebase variables whenever a database operation
    *  is required.
    */

    // firebase variables
    public static DatabaseReference mDatabase;
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mFirebaseDatabase;
    public static FirebaseStorage mStorage;
    // userID
    public static String userId;
    // holds the most recent run stats used by runSummary
    public static MutableLiveData<ArrayList<String>> recentRun = new MutableLiveData<ArrayList<String>>();
    // gets all times run in epoch format
    public static MutableLiveData<ArrayList<String>> epochTimes = new MutableLiveData<ArrayList<String>>();

    // constructor for static members
    static {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

    public static void insertRunDirectory() {
        DatabaseReference destination = mDatabase.child("Users")
                .child(userId)
                .child("Runs");
    }

    // insert run to firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void insertRun(long label, Run r) {
        // reference runs
        DatabaseReference destination = mDatabase.child("Users")
                .child(userId)
                .child("Runs");

        // reference bitmap from run
        Bitmap runMap = r.getRouteImg();
        // declare array output stream, compress to png and output to stream
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        runMap.compress(Bitmap.CompressFormat.PNG, 100, bao);
        runMap.recycle();
        // convert stream to byte array
        byte[] runImgArray = bao.toByteArray();
        // encode into base 64 string
        String image64 = Base64.getEncoder().encodeToString(runImgArray);

        // saved run values to firebase destination
        destination.child(String.valueOf(label)).child("bitmap").setValue(image64);
        destination.child(String.valueOf(label)).child("distance").setValue(r.getDistance());
        destination.child(String.valueOf(label)).child("speed").setValue(r.getSpeed());
        destination.child(String.valueOf(label)).child("duration").setValue(r.getRunDuration());
    }

    // adds a stat to firebase
    public static void addStat(long label, int statNumber, Float[] statList) {
        // reference stat location in firebase
        DatabaseReference destination = mDatabase.child("Users")
                .child(userId)
                .child("Runs")
                .child(String.valueOf(label))
                .child("statDump")
                .child(String.valueOf(statNumber));

        // save stats to firebase
        destination.child("Distance Interval (km)").setValue(String.valueOf(statList[0]));
        destination.child("Time elapsed (min)").setValue(String.valueOf(statList[1]));
        destination.child("Speed (min per km)").setValue(String.valueOf(statList[2]));
    }

    // gets all run epoch times and saves it to epochTimes
    public static void updateRunEpochTimes() {
        // reference run destination
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("Runs");

        // used to retrieve all children from reference location in fb
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // holds all epoch times
                ArrayList<String> a = new ArrayList<String>();
                // add all epoch times to Arraylist
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    a.add(snapshot1.getKey());
                }
                // post epoch times to epochtimes allows observers of this to detect changes
                epochTimes.postValue(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle when cancelled
            }
        });
    }

    // gets a run by the epoch time param
    public static void getRunByEpoch(String epochTime) {
        // reference the epoch time
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("Runs")
                .child(epochTime);

        // gets all children from the individual run
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // holds all stats
                ArrayList<String> a = new ArrayList<String>();
                // add all stats to ArrayList
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    a.add((String.valueOf(snapshot1.getValue())));
                }
                // adds the epoch time with stats as an identifier
                a.add(epochTime);
                // post stats to recent run so observers can detect changes to it
                recentRun.postValue(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // inits stats used for saving stats whilst on run
    public static void initStat(long label) {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("Runs")
                .child(String.valueOf(label));
    }

    // adds points to profile from points param
    public static void addPointsForRun(int points) {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("points");

        // gets elements needed for points
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sp : snapshot.getChildren()) {
                    // if key equal any field needed
                    if (sp.getKey().equals("availpoints")) {
                        // get existing value
                        int spValue = 0;
                        if (sp.getValue().getClass() == Long.class) {
                            String spValueStr = sp.getValue().toString();
                            spValue = Integer.valueOf(spValueStr);
                        }
                        // increment values by points added
                        reference.child("availpoints").setValue(spValue + points);
                    }
                    else if (sp.getKey().equals("tierpoints")) {
                        // get existing value
                        int spValue = 0;
                        if (sp.getValue().getClass() == Long.class) {
                            String spValueStr = sp.getValue().toString();
                            spValue = Integer.valueOf(spValueStr);
                        }
                        // increment values by points added
                        reference.child("tierpoints").setValue(spValue + points);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // adds rewards for run
    public static void addRewardsForRun(int reward) {
        // get awards reference
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("points")
                .child("awards");

        // gets awards children
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // convert rewards to string
                String rewardStr = reward + "k";
                for (DataSnapshot sp : snapshot.getChildren()) {
                    // get the existing value for reward
                    if (sp.getKey().equals(rewardStr)) {
                        // init existing reward
                        int existingReward = 0;
                        if (sp.getValue().getClass() == Long.class) {
                            String spValueStr = sp.getValue().toString();
                            existingReward = Integer.valueOf(spValueStr);
                        }
                        // increment value by reward
                        reference.child(rewardStr).setValue(++existingReward);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
