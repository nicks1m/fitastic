package com.example.fitastic;

public class RegisterUser {
    private int id;
    private String name;
    private String password;
    private String email;
    private String dob;

    public RegisterUser(int id, String name, String password, String email, String dob){
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.dob = dob;
    }

    public RegisterUser(){
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "RegisterUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
}
