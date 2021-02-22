package com.example.fitastic;

public class dietModel {
    private String name;
    private int cal;

    public dietModel() {
    }

    public dietModel(String name, int cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }
}
