package com.example.updatepatients.entity;

public class HashRequest {
    private String password;

    public HashRequest(String password) {
        this.password = password;
    }

    // Getters y Setters

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
