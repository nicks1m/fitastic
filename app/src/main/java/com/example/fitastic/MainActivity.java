package com.example.fitastic;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    private EditText userIn;
    private EditText pwIn;
    private EditText mailIn;
    private EditText dobIn;
    private TextView pwError;
    private TextView mailError;
    private Button btnReg;
    private String pwCheck;
    private String mailCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeApp();
    }

    private void initializeApp() {
        userIn = (EditText) findViewById(R.id.username);
        pwIn = (EditText) findViewById(R.id.password);
        mailIn = (EditText) findViewById(R.id.email);
        dobIn = (EditText) findViewById(R.id.dateofbirth);
        pwError = (TextView) findViewById(R.id.pwError);
        mailError = (TextView) findViewById(R.id.mailError);
//        btnReg = (Button) findViewById(R.id.btnRegister);
//        btnReg.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                userRegister();
//            }
//        });
    }

    public boolean userRegister(View v) {
        //1.Check if fields are meet requirements if not throw error
        pwCheck = pwIn.getText().toString();
        mailCheck = mailIn.getText().toString();
        //If both requirements met, hide errors, send data to sql and proceed to next page
        if (checkPw(pwCheck) && checkMail(mailCheck)) {
            pwError.setVisibility(View.INVISIBLE);
            mailError.setVisibility(View.INVISIBLE);
            System.out.println("Successful");
            return true;
        }

        System.out.println("Password Failed / Mail Failed");
        return false;

    }

    //Check Password if meet requirements
    public boolean checkPw(String password) {

        //Array Inits
        char[] symbolsArray = {'!', '@', '#', '$', '%', '^', '&', '/', '?'};
        char[] digitsArray = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] alphabetsArray = new char[52];
        //Populate alphabetsArray with Alphabets
        char count = 65;
        for (int i = 0; i < 52; i++) {

            alphabetsArray[i] = count++;
            if (i > 25) {
                count = 97;
                for (i = 25; i < 52; i++) {
                    alphabetsArray[i] = count++;
                }
            }

        }
        /* Start of Counting */

        //Init COUNTERS
        String outcome = "";
        int symbolCounter = 0;
        int digitCounter = 0;
        boolean lowerCase = false;
        boolean upperCase = false;

        //Traverse Array & Count number of symbols
        for (int i = 0; i < password.length(); i++) {
            for (int j = 0; j < symbolsArray.length; j++) {
                if (password.charAt(i) == symbolsArray[j]) {
                    symbolCounter++;
                }
            }
        }

        //Traverse Array & Count number of symbols
        for (int i = 0; i < password.length(); i++) {
            for (int j = 0; j < digitsArray.length; j++) {
                if (password.charAt(i) == digitsArray[j]) {
                    digitCounter++;
                }
            }
        }
        //Check for presence of uppercase
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                upperCase = true;
                break;
            }
        }

        //Check for presence of lowercase
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                lowerCase = true;
                break;
            }
        }
//        System.out.println("password length " + password.length());
//        System.out.println("number of symbols " + symbolCounter);
//        System.out.println("number of digits " + digitCounter);
//        System.out.println("there is a lowercase " + lowerCase);
//        System.out.println("there is a upper case " + upperCase);
//
        //If Else
        if (password.length() >= 8 && symbolCounter >= 1 && digitCounter >= 1 && (lowerCase && upperCase)) {
            return true;
        } else
            pwError.setVisibility(View.VISIBLE);
        return false;

    }

    //Check E-Mail if meet requirements(req. @ in the field, and .com .uk .sg etc..)

    public boolean checkMail(String mail) {
        System.out.println("working");
        for (int i = 0; i < mail.length(); i++) {
            if (mail.charAt(i) == '@') {
                System.out.println("Mail Pass");
                return true;
            } else
                System.out.println("Mail Fail");
            mailError.setVisibility(View.VISIBLE);
        }
        return false;
    }

    //Check Username if meet requirements (min. 5 char)

}