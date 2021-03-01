package com.example.fitastic.repositories;

import android.graphics.Bitmap;

import com.example.fitastic.models.Run;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class mainRepository {

    public static DatabaseReference mDatabase;
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mFirebaseDatabase;
    public static FirebaseStorage mStorage;

    // constructor for static members
    static {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public static void insertRun(Run r) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference destination = mDatabase.child("Users").child(userId).child("Runs");
        String key = generateKey(destination.push().getKey());

        destination.child(key).child("bitmap").setValue(r.getRouteImg());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = r.getRouteImg();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        ArrayList<Bitmap> list = new ArrayList<Bitmap>();

        //UploadTask task = ref.putBytes(data);

        destination.child(key).child("distance").setValue(r.getDistance());
        destination.child(key).child("duration").setValue(r.getRunDuration());
        destination.child(key).child("date/time").setValue(r.getTime());
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
}
