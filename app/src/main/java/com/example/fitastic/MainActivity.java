package com.example.fitastic;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    private EditText userIn;
    private EditText pwIn;
    private EditText mailIn;
    private EditText dobIn;
    private Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initializeApp();
    }

    private void initializeApp(){
        userIn = (EditText) findViewById(R.id.username);
        pwIn = (EditText) findViewById(R.id.password);
        mailIn = (EditText) findViewById(R.id.email);
        dobIn = (EditText) findViewById(R.id.dateofbirth);
        btnReg = (Button) findViewById(R.id.btnRegister);
//        btnReg.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                userRegister();
//            }
//        });
    }

    public static boolean userRegister(View v){
      //1.Check if fields are meet requirements if not throw error
        System.out.println("WOots");
        return true;
    }

}