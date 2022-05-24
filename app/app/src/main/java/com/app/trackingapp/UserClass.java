package com.app.trackingapp;

public class UserClass {

    String username, email, password, gender;
    Double km, weight;
    public UserClass() {}

    public UserClass(String username,  String email, String password, Double weight, String gender, Double km) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.weight = weight;
        this.gender = gender;
        this.km = km;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setKm(Double km) { this.km = km; }

    public Double getKm() { return this.km; }
}
