package com.app.trackingapp.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.app.trackingapp.DialogClass;
import com.app.trackingapp.MapsActivity;
import com.app.trackingapp.R;
import com.app.trackingapp.RegisterActivity;
import com.app.trackingapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private FirebaseAuth mAuth;

    private EditText regEmail, regPassword;
    private TextView forgPassword;

    private Button lbutton;
    private Button rbutton;

    private DialogClass dc;
    ActivityResultLauncher<String[]> locationPermissionRequest;

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// TODO move code so that in mapready
        // check if app has fine location access
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // we got permission
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // got only approximate location
                        dc =  new DialogClass("Location Permission", "This message appeared because you only granted" +
                                " approximate Location access. But in order to properly use this Application you need to provice " +
                                "fine Location access.\n");
                        dc.show(getSupportFragmentManager(), "no-permission");
                        terminateApp();
                    } else {
                        dc =  new DialogClass("Location Permission", "This message appeared because you only granted" +
                                " approximate Location access. But in order to properly use this Application you need to provice " +
                                "fine Location access.\n");
                        dc.show(getSupportFragmentManager(), "no-permission");
                        terminateApp();
                    }
                }
        );

        regEmail = findViewById(R.id.email);
        regPassword = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

       // final TextView welcomeText = (TextView)findViewById(R.id.welcometext);

        // ref.addValueEventListener(new ValueEventListener() {
           // @Override
          //  public void onDataChange(DataSnapshot snapshot) {
          //      String x = snapshot.getValue(String.class);
          //      welcomeText.setText(x);
         //   }

       //     @Override
      //      public void onCancelled(DatabaseError error) {

      //      }
     //   });

       // Toast.makeText( LoginActivity.this, "Firebase conected", Toast.LENGTH_LONG).show();


        //forgot password
        forgPassword = (TextView) findViewById(R.id.forgotPassword);
        forgPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = regEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    regEmail.setError("please enter your email");
                    regEmail.requestFocus();
                 //  return;
                } else {
                    mAuth.sendPasswordResetEmail(email);
                    Toast.makeText(LoginActivity.this, "you can change your password using the link in your email", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Login Button click listener(1)
        lbutton = (Button) findViewById(R.id.login);
        lbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    regEmail.setError("Email cannot be empty");
                    regEmail.requestFocus();
                }  else {

                    // Authentification Pr??fung
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(view.getContext(), MapsActivity.class);
                                i.putExtra("mail", email);
                                startActivity(i);
                                Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        rbutton = (Button) findViewById(R.id.registrieren);
        rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), RegisterActivity.class);
                startActivity(i);
            }
        });


        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                //loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });


    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void terminateApp() {
        System.exit(0);
    }
}