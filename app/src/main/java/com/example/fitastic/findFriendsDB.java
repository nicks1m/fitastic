package com.example.fitastic;

public class findFriendsDB {

    private String display_name, dob, bmi;

    public findFriendsDB(){


    }

    public findFriendsDB(String display_name, String name, String dob, String bmi) {
        this.display_name = display_name;
        this.dob = dob;
        this.bmi = bmi;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }
}
