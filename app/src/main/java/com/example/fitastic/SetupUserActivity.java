package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SetupUserActivity extends AppCompatActivity {

    private Button go_bmi;
    private EditText displayName;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user);
        Bundle bundle = getIntent().getExtras();
        String uid = bundle.getString("ID");
        System.out.println("uid on setupuser" + uid);

        go_bmi = findViewById(R.id.go_bmi);
        displayName = findViewById(R.id.displayName);


        go_bmi.setOnClickListener(v->{
            if(!TextUtils.isEmpty(displayName.getText())){

                String dName = displayName.getText().toString();
                //to-add : pass display name into firebase database
                writeUserDisplay(uid,dName);
                //Open BMI Page
                openBMI();

            }
        });


    }

    public void writeUserDisplay(String userId, String dname) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userId).child("displayname").setValue(dname);
    }


    public void openBMI(){
        Intent intent = new Intent(this, BMIActivity.class);
        startActivity(intent);
    }


}