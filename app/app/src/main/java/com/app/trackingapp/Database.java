package com.app.trackingapp;

public class Database {

    /*
    private static final String DATABASE_LINK = "https://test";
    FirebaseDatabase database = FirebaseDatabase.getInstance("DATABASE_LINK");
    DatabaseReference myRef = database.getReference("message_2");
    DatabaseReference myRef2 = database.getReference("kunden");
    myRef2.child("1").setValue(k);
    */

    public static UserClass login(String email, String password) {
        return null;
    }

    public static void register(String email, String password){

    }

    public static void update(UserClass user) {
        if(login(user.getEmail(), user.getPassword())==null){
            return;
        }
    }


}
