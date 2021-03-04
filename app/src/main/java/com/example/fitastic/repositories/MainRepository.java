package com.example.fitastic.repositories;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.fitastic.models.Run;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MainRepository {

    public static DatabaseReference mDatabase;
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mFirebaseDatabase;
    public static FirebaseStorage mStorage;
    public static String userId;

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
        String key = r.getTime().toString();

        DatabaseReference destination = mDatabase.child("Users")
                .child(userId)
                .child("Runs");

        Bitmap runMap = r.getRouteImg();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        runMap.compress(Bitmap.CompressFormat.PNG, 100, bao);
        runMap.recycle();
        byte[] runImgArray = bao.toByteArray();
        String image64 = Base64.getEncoder().encodeToString(runImgArray);

        destination.child(key).child("bitmap").setValue(image64);
        destination.child(key).child("distance").setValue(r.getDistance());
        destination.child(key).child("speed").setValue(r.getSpeed());
        destination.child(key).child("duration").setValue(r.getRunDuration());
    }

    public static String generateKey(String input) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) > 32 && input.charAt(i) < 91 ||
            input.charAt(i) > 96 && input.charAt(i) < 123) {
                key.append(input.charAt(i));
            }
        }
        return key.toString();
    }

    public String getDateTimeLatestRun() {
        DatabaseReference reference = mDatabase.child("users")
                .child(userId)
                .child("Runs");
        ArrayList<String> dateTimes = new ArrayList<String>();


        // unfinished
        return new String();
    }

    public static String getBase64String(String id) {

        // unfinished
        return new String();
    }
}
