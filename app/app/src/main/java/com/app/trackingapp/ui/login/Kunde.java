package com.app.trackingapp.ui.login;

public class Kunde {

    private String email;
    private String username;
    private String password;
    private boolean gender;
    private double weight;
    private double distance;
    private int rang;

    public Kunde(String email, String username, String password, boolean gender, double weight, double distance, int rang) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.weight = weight;
        this.distance = distance;
        this.rang = rang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }
}
