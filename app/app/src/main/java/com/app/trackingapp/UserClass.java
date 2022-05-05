package com.app.trackingapp;

public class UserClass {

    String username, email, password, weight, gender;


    public UserClass(String username,  String email, String password, String weight, String gender) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.weight = weight;
        this.gender = gender;
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String isGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
