package com.example.cs408_app.Model;

public class User {

    String name;
    String email;
    String phone_number;
    String created_at;
    String token;
//    boolean kaistian;

    public User(String name, String email, String phone_number) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getEmail() {return this.email;}
}