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
    public static void insertRun(Run r) {
        // stores time/date in epoch time (form 1614945287)
        long label = Instant.now().toEpochMilli();

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
//                    if (snapshot1.getValue() instanceof Double) {
//                        a.add(String.valueOf( snapshot1.getValue());
//                    }
                    a.add((String.valueOf(snapshot1.getValue())));
                }

                recentRun.postValue(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
