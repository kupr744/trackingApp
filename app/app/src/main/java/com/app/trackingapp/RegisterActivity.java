package com.app.trackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.trackingapp.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText regUsername, regEmail, regPassword, regRPassword, regWeight;
    private Button r2button;
    RadioButton male, female;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUsername = findViewById(R.id.usernametext);
        regEmail = findViewById(R.id.emailtext);
        regPassword = findViewById(R.id.passwordtext);
        regRPassword = findViewById(R.id.rpasswordtext);
        regWeight = findViewById(R.id.weighttext);
        male = findViewById(R.id.radioButtonMale);
        female = findViewById(R.id.radioButtonFemale);

        mAuth = FirebaseAuth.getInstance();

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
              //  String r1 = male.getText().toString();
                // String r2 = female.getText().toString();
                String gender;
                if (male.isChecked()){
                    gender = "male";
                } else {
                    gender = "female";
                }


                if (TextUtils.isEmpty(username)) {
                    regUsername.setError("Username cannot be empty");
                    regUsername.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                        regEmail.setError("Email cannot be empty");
                        regEmail.requestFocus();
                    } else if (TextUtils.isEmpty(weight)) {
                        regWeight.setError("Weight cannot be empty");
                        regWeight.requestFocus();
                    } else if (password.length() < 5 ) {
                        regPassword.setError("Password must be >5 characters");
                        regPassword.requestFocus();
                    } else if (!password.equals(rpassword)) {
                        regRPassword.setError("Password is not equal");
                        regRPassword.requestFocus();
                    } else {



                    // Authentification
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Nutzer wird in Realtime Database geschrieben
                                Double km = 0.0;
                                UserClass user = new UserClass(username, email, Double.valueOf(weight), gender, km);
                                reference.child(username).setValue(user);

                                Intent i = new Intent(view.getContext(), LoginActivity.class);
                                startActivity(i);
                                Toast.makeText(RegisterActivity.this, "successful registration", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Regestration Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            }

        });
    }

}