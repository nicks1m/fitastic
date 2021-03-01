package com.example.fitastic.diet;

public class dietModel {
    private String name;
    private String cal;

    public dietModel() {
    }

    public dietModel(String name, String cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCal() {
        return cal;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }
}
