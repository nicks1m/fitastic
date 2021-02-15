package com.example.fitastic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {

    //Create Buttons for page
    Button regButton;
    Button loginButton;
    Button forgotPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();


        regButton = (Button) findViewById(R.id.btn_goto_register);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity2();
            }
        });
        forgotPassword = (Button) findViewById(R.id.btn_forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reset = new EditText(v.getContext());
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(v.getContext());
                resetDialog.setTitle("Reset your password using your email adress: ");
                resetDialog.setMessage("Enter your email adress: ");
                resetDialog.setView(reset);

                resetDialog.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send the reset email
                        String mail = reset.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset password email sent successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Failure to send the email! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                resetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no action
                    }
                });

                resetDialog.create().show();

            }
        });
    }

    //Opens Register Activity Page
    public void openNewActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //Opens Home Activity Page
    public void openNewActivity2(){
        Intent intent = new Intent(this, NavPage.class);
        startActivity(intent);
    }

}

