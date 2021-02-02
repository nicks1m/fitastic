package com.example.fitastic;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String REGISTER_TABLE = "REGISTER_TABLE";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_DOB = "DOB";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "records.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + REGISTER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_EMAIL + " TEXT, " + COLUMN_DOB + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(RegisterUser registerUser){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, registerUser.getName());
        cv.put(COLUMN_PASSWORD, registerUser.getPassword());
        cv.put(COLUMN_EMAIL, registerUser.getEmail());
        cv.put(COLUMN_DOB, registerUser.getDob());

        long insert = db.insert(REGISTER_TABLE, null, cv);

        if(insert == -1){
            return false;
        }else{
            return true;
        }
    }
}
