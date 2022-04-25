package com.app.trackingapp;

import com.app.trackingapp.ui.login.Kunde;

public class Database {

    /*
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://testfirebase-814f0-default-rtdb.firebaseio.com/");
    DatabaseReference myRef = database.getReference("message_2");
    DatabaseReference myRef2 = database.getReference("kunden");
    myRef2.child("1").setValue(k);
    */

    public static Kunde login(String email, String password) {
        return null;
    }

    public static void register(String email, String password){

    }

    public static void update(Kunde kunde) {
        if(login(kunde.getEmail(), kunde.getPassword())==null){
            return;
        }
    }
}
