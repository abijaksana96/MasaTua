package com.example.masatua.models;

public class User {
    public String name;
    public String email;

    public User() {} // Wajib untuk Firestore
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}