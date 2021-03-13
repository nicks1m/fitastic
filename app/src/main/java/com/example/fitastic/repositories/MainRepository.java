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
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class MainRepository {

    public static DatabaseReference mDatabase;
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mFirebaseDatabase;
    public static FirebaseStorage mStorage;
    public static String userId;

    // access point for epochTimes on each run

    // constructor for static members
    static {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

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

        destination.child(String.valueOf(label)).child("bitmap").setValue(image64);
        destination.child(String.valueOf(label)).child("distance").setValue(r.getDistance());
        destination.child(String.valueOf(label)).child("speed").setValue(r.getSpeed());
        destination.child(String.valueOf(label)).child("duration").setValue(r.getRunDuration());
    }

    public static void addStat(long label, int statNumber, Float[] statList) {
        DatabaseReference destination = mDatabase.child("Users")
                .child(userId)
                .child("Runs")
                .child(String.valueOf(label))
                .child("statDump")
                .child(String.valueOf(statNumber));

        destination.child("Distance Interval (km)").setValue(String.valueOf(statList[0]));
        destination.child("Time elapsed (min)").setValue(String.valueOf(statList[1]));
        destination.child("Speed (min per km)").setValue(String.valueOf(statList[2]));
    }

    public static MutableLiveData<ArrayList<String>> epochTimes = new MutableLiveData<ArrayList<String>>();;

    public static void updateRunEpochTimes() {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("Runs");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> a = new ArrayList<String>();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    a.add(snapshot1.getKey());
                }

                epochTimes.postValue(a);
                int x = 5;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle when cancelled
            }
        });
    }

    public static MutableLiveData<ArrayList<String>> recentRun = new MutableLiveData<ArrayList<String>>();

    public static void getRunByEpoch(String epochTime) {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("Runs")
                .child(epochTime);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> a = new ArrayList<String>();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    a.add((String.valueOf(snapshot1.getValue())));
                }
                a.add(epochTime);

                recentRun.postValue(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void initStat(long label) {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("Runs")
                .child(String.valueOf(label));
    }

    public static void addPointsForRun(int points) {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("points");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sp : snapshot.getChildren()) {
                    if (sp.getKey().equals("availpoints")) {
                        int spValue = 0;
                        if (sp.getValue().getClass() == Long.class) {
                            String spValueStr = sp.getValue().toString();
                            spValue = Integer.valueOf(spValueStr);
                        }
                        reference.child("availpoints").setValue(spValue + points);
                    }
                    else if (sp.getKey().equals("tierpoints")) {
                        int spValue = 0;
                        if (sp.getValue().getClass() == Long.class) {
                            String spValueStr = sp.getValue().toString();
                            spValue = Integer.valueOf(spValueStr);
                        }
                        reference.child("tierpoints").setValue(spValue + points);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void addRewardsForRun(int reward) {
        DatabaseReference reference = mDatabase.child("Users")
                .child(userId)
                .child("points")
                .child("awards");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rewardStr = reward + "k";
                for (DataSnapshot sp : snapshot.getChildren()) {
                    if (sp.getKey().equals(rewardStr)) {
                        reference.child(rewardStr).setValue(1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
