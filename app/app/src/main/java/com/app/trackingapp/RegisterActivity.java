package com.app.trackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.trackingapp.ui.login.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText regUsername, regEmail, regPassword, regRPassword, regWeight;
    private Button r2button;

    FirebaseDatabase rootNode;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUsername = findViewById(R.id.usernametext);
        regEmail = findViewById(R.id.emailtext);
        regPassword = findViewById(R.id.passwordtext);
        regRPassword = findViewById(R.id.rpasswordtext);
        regWeight = findViewById(R.id.weighttext);


        r2button = (Button) findViewById(R.id.registrieren2);
        r2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");

                String username = regUsername.getText().toString();
                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();
                String rpassword = regRPassword.getText().toString();
                String weight = regWeight.getText().toString();


                if (TextUtils.isEmpty(username)) {
                    regUsername.setError("Username cannot be empty");
                    regUsername.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                        regEmail.setError("Email cannot be empty");
                        regEmail.requestFocus();
                    } else if (TextUtils.isEmpty(weight)) {
                        regWeight.setError("Weight cannot be empty");
                        regWeight.requestFocus();
                    } else if (TextUtils.isEmpty(password)) {
                        regPassword.setError("Password cannot be empty");
                        regPassword.requestFocus();
                    } else if (TextUtils.isEmpty(rpassword)) {
                        regRPassword.setError("Repeat Password cannot be empty");
                        regRPassword.requestFocus();
                    } else if (!password.equals(rpassword)) {
                        regPassword.setError("Password is not correct");
                        regPassword.requestFocus();
                    } else {
                    UserClass userClass = new UserClass(username, email, password, weight);
                    reference.child(username).setValue(userClass);

                    Intent i = new Intent(view.getContext(), LoginActivity.class);
                    startActivity(i);

                    Toast.makeText(RegisterActivity.this, "successful registration", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}