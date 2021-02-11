package com.example.fitastic;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    private EditText userIn;
    private EditText pwIn;
    private EditText pwIn2;
    private EditText mailIn;
    private EditText dobIn;
    private TextView pwError;
    private TextView pwError2;
    private TextView mailError;
    private TextView userError;
    private TextView dobError;
    private Button btnReg;
    private String pwCheck;
    private String mailCheck;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userIn = findViewById(R.id.username);
        pwIn = findViewById(R.id.password);
        pwIn2 = findViewById(R.id.password2);
        mailIn = findViewById(R.id.email);
        dobIn = findViewById(R.id.dateofbirth);
        pwError = findViewById(R.id.pwError);
        pwError2 = findViewById(R.id.pwError2);
        mailError = findViewById(R.id.mailError);
        userError = findViewById(R.id.userError);
        dobError = findViewById(R.id.dobError);
        btnReg = findViewById(R.id.btnReg);
        auth = FirebaseAuth.getInstance();


        btnReg.setOnClickListener(v -> {
            String register_pw = pwIn.getText().toString();
            String register_email = mailIn.getText().toString();

            if (!checkDetails() || TextUtils.isEmpty(register_email) || TextUtils.isEmpty(register_pw)) {
                Toast.makeText(RegisterActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(register_email, register_pw);
//                public void onClick(View v){}
                 openSetupActivity();
            }
        });
    }

    public void openSetupActivity(){
        Intent intent = new Intent(this, SetupUserActivity.class);
        startActivity(intent);
    }

    public void registerUser (String mailIn, String pwIn){

        auth.createUserWithEmailAndPassword(mailIn, pwIn).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registering user successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean checkDetails(){

        //userIn, mailIn, pwIn, dobIn
        String u = userIn.getText().toString();
        String m = mailIn.getText().toString();
        String p = pwIn.getText().toString();
        String p2 = pwIn2.getText().toString();
        String d = dobIn.getText().toString();

        boolean ucheck = false;
        boolean mcheck = false;
        boolean pcheck = false;
        boolean pcheck2 = false;
        boolean dcheck = false;


        //Checks individual fields to see if meets criteria, show error if not.
        if(userIn == null || u.length() < 5 || !checkUser(u)){
            userError.setVisibility(View.VISIBLE);
            ucheck = false;
        }else{
            userError.setVisibility(View.INVISIBLE);
            ucheck = true;
        }

        if(!checkMail(m) || m.length()<5){
            mailError.setVisibility(View.VISIBLE);
            mcheck = false;
        } else {
            mailError.setVisibility(View.INVISIBLE);
            mcheck = true;

        }

        if(!checkPw(p)){
            pwError.setVisibility(View.VISIBLE);
            pcheck = false;
        } else {
            pwError.setVisibility(View.INVISIBLE);
            pcheck = true;
        }

        if(!p.contentEquals(p2)){
            pwError2.setVisibility(View.VISIBLE);
            pcheck2 = false;
        } else  {
            pwError2.setVisibility(View.INVISIBLE);
            pcheck2 = true;
        }

        if(dobIn == null || d.length() < 6){
            dobError.setVisibility(View.VISIBLE);
            dcheck = false;
        } else {
            dobError.setVisibility(View.INVISIBLE);
            dcheck = true;
        }

        //If one field fails the criteria, registration wont go through

        if(pcheck == false || mcheck == false || dcheck == false || ucheck == false || pcheck2 == false){
            return false;
        }
        return true;
    }


    public boolean checkUser(String user){
        char[] symbolsArray = {'~','`','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[','}',']','|',':',';','"','<','>','.','?','/'};
        for(int i = 0; i < user.length(); i++){
            for(int j = 0; j < symbolsArray.length; j++){
                if(user.charAt(i) == symbolsArray[j]){

                    return false;
                }
            }
        }
        return true;
    }

//    public boolean rePw(){
//        if(pwIn.getText().toString() != pwIn2.getText().toString()){
//            System.out.println(false);
//          return false;
//        }
//        System.out.println(true);
//        return true;
//    }

    //Check Password if meet requirements
    public boolean checkPw(String password) {

        //Array Inits
        char[] symbolsArray = {'~','`','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[','}',']','|',':',';','"','<','>','.','?','/'};
        char[] digitsArray = {'0','1', '2', '3', '4', '5', '6', '7', '8', '9'};
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
//            pwError.setVisibility(View.VISIBLE);
            return false;

    }

    //Check E-Mail if meet requirements(req. @ in the field, and .com .uk .sg etc..)

    public boolean checkMail(String mail) {
        for (int i = 0; i < mail.length(); i++) {
            if (mail.charAt(i) == '@') {
                System.out.println("Mail Pass");
                return true;
            } else
                System.out.println("Mail Fail");
//            mailError.setVisibility(View.VISIBLE);
        }
        return false;
    }
}










































//package com.example.fitastic;
//
//import android.content.Intent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.Toast;
//
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private EditText userIn;
//    private EditText pwIn;
//    private EditText pwIn2;
//    private EditText mailIn;
//    private EditText dobIn;
//    private TextView pwError;
//    private TextView pwError2;
//    private TextView mailError;
//    private TextView userError;
//    private TextView dobError;
//    private Button btnReg;
//    private String pwCheck;
//    private String mailCheck;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//        initializeApp();
//    }
//
//    private void initializeApp() {
//        userIn = findViewById(R.id.username);
//        pwIn = findViewById(R.id.password);
//        pwIn2 = findViewById(R.id.password2);
//        mailIn = findViewById(R.id.email);
//        dobIn = findViewById(R.id.dateofbirth);
//        pwError = findViewById(R.id.pwError);
//        pwError2 = findViewById(R.id.pwError2);
//        mailError = findViewById(R.id.mailError);
//        userError = findViewById(R.id.userError);
//        dobError = findViewById(R.id.dobError);
//        btnReg = findViewById(R.id.btnReg);
//        btnReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RegisterUser user;
//                if(checkDetails()){
//                    System.out.println(checkDetails());
//                    user = new RegisterUser(-1, userIn.getText().toString(), pwIn.getText().toString(), mailIn.getText().toString(), dobIn.getText().toString());
//                    DatabaseHelper databaseHelper = new DatabaseHelper(RegisterActivity.this);
//
//                    boolean success = databaseHelper.addOne(user);
//
//                    Toast.makeText(RegisterActivity.this, "Successful Registration ", Toast.LENGTH_SHORT).show();
//                    openBMIactivity();
//                } else{
//                    Toast.makeText(RegisterActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
//                }
////                try {
////                    user = new RegisterUser(-1, userIn.getText().toString(), pwIn.getText().toString(), mailIn.getText().toString(), dobIn.getText().toString());
////                    Toast.makeText(RegisterActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
////                }catch (Exception e){
////                    Toast.makeText(RegisterActivity.this, "Error creating user", Toast.LENGTH_SHORT).show();
////                    user = new RegisterUser(-1, "error", "error", "error", "error");
////                }
//
//
//            }
//        });
//    }
//
//    public boolean checkDetails(){
//
//        //userIn, mailIn, pwIn, dobIn
//        String u = userIn.getText().toString();
//        String m = mailIn.getText().toString();
//        String p = pwIn.getText().toString();
//        String p2 = pwIn2.getText().toString();
//        String d = dobIn.getText().toString();
//
//        boolean ucheck = false;
//        boolean mcheck = false;
//        boolean pcheck = false;
//        boolean pcheck2 = false;
//        boolean dcheck = false;
//
//
//        //Checks individual fields to see if meets criteria, show error if not.
//        if(userIn == null || u.length() < 5 || !checkUser(u)){
//            userError.setVisibility(View.VISIBLE);
//            ucheck = false;
//        }else{
//            userError.setVisibility(View.INVISIBLE);
//            ucheck = true;
//        }
//
//        if(!checkMail(m) || m.length()<5){
//            mailError.setVisibility(View.VISIBLE);
//            mcheck = false;
//        } else {
//            mailError.setVisibility(View.INVISIBLE);
//            mcheck = true;
//
//        }
//
//        if(!checkPw(p)){
//            pwError.setVisibility(View.VISIBLE);
//            pcheck = false;
//        } else {
//            pwError.setVisibility(View.INVISIBLE);
//            pcheck = true;
//        }
//
//        if(!p.contentEquals(p2)){
//            pwError2.setVisibility(View.VISIBLE);
//            pcheck2 = false;
//        } else  {
//            pwError2.setVisibility(View.INVISIBLE);
//            pcheck2 = true;
//        }
//
//        if(dobIn == null || d.length() < 6){
//            dobError.setVisibility(View.VISIBLE);
//            dcheck = false;
//        } else {
//            dobError.setVisibility(View.INVISIBLE);
//            dcheck = true;
//        }
//
//        //If one field fails the criteria, registration wont go through
//
//        if(pcheck == false || mcheck == false || dcheck == false || ucheck ==false || pcheck2 == false){
//            return false;
//        }
//        return true;
//    }
//
//
//    public boolean checkUser(String user){
//        char[] symbolsArray = {'~','`','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[','}',']','|',':',';','"','<','>','.','?','/'};
//        for(int i = 0; i < user.length(); i++){
//            for(int j = 0; j < symbolsArray.length; j++){
//                if(user.charAt(i) == symbolsArray[j]){
//
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
////    public boolean rePw(){
////        if(pwIn.getText().toString() != pwIn2.getText().toString()){
////            System.out.println(false);
////          return false;
////        }
////        System.out.println(true);
////        return true;
////    }
//
//    //Check Password if meet requirements
//    public boolean checkPw(String password) {
//
//        //Array Inits
//        char[] symbolsArray = {'~','`','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[','}',']','|',':',';','"','<','>','.','?','/'};
//        char[] digitsArray = {'0','1', '2', '3', '4', '5', '6', '7', '8', '9'};
//        char[] alphabetsArray = new char[52];
//
//        //Populate alphabetsArray with Alphabets
//        char count = 65;
//        for (int i = 0; i < 52; i++) {
//            alphabetsArray[i] = count++;
//            if (i > 25) {
//                count = 97;
//                for (i = 25; i < 52; i++) {
//                    alphabetsArray[i] = count++;
//                }
//            }
//
//        }
//        /* Start of Counting */
//
//        //Init COUNTERS
//        String outcome = "";
//        int symbolCounter = 0;
//        int digitCounter = 0;
//        boolean lowerCase = false;
//        boolean upperCase = false;
//
//        //Traverse Array & Count number of symbols
//        for (int i = 0; i < password.length(); i++) {
//            for (int j = 0; j < symbolsArray.length; j++) {
//                if (password.charAt(i) == symbolsArray[j]) {
//                    symbolCounter++;
//                }
//            }
//        }
//
//        //Traverse Array & Count number of symbols
//        for (int i = 0; i < password.length(); i++) {
//            for (int j = 0; j < digitsArray.length; j++) {
//                if (password.charAt(i) == digitsArray[j]) {
//                    digitCounter++;
//                }
//            }
//        }
//        //Check for presence of uppercase
//        for (int i = 0; i < password.length(); i++) {
//            if (Character.isUpperCase(password.charAt(i))) {
//                upperCase = true;
//                break;
//            }
//        }
//
//        //Check for presence of lowercase
//        for (int i = 0; i < password.length(); i++) {
//            if (Character.isLowerCase(password.charAt(i))) {
//                lowerCase = true;
//                break;
//            }
//        }
////        System.out.println("password length " + password.length());
////        System.out.println("number of symbols " + symbolCounter);
////        System.out.println("number of digits " + digitCounter);
////        System.out.println("there is a lowercase " + lowerCase);
////        System.out.println("there is a upper case " + upperCase);
////
//        //If Else
//        if (password.length() >= 8 && symbolCounter >= 1 && digitCounter >= 1 && (lowerCase && upperCase)) {
//            return true;
//        } else
////            pwError.setVisibility(View.VISIBLE);
//        return false;
//
//    }
//
//    //Check E-Mail if meet requirements(req. @ in the field, and .com .uk .sg etc..)
//
//    public boolean checkMail(String mail) {
//        for (int i = 0; i < mail.length(); i++) {
//            if (mail.charAt(i) == '@') {
//                System.out.println("Mail Pass");
//                return true;
//            } else
//                System.out.println("Mail Fail");
////            mailError.setVisibility(View.VISIBLE);
//        }
//        return false;
//    }
//
//    //Check Username if meet requirements (min. 5 char)
//
//    public void openBMIactivity(){
//        Intent intent = new Intent(this, BMIActivity.class);
//        startActivity(intent);
//    }
//
//}