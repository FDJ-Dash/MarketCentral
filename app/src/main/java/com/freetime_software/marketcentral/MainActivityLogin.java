package com.freetime_software.marketcentral;

import android.content.Intent;
//import android.content.res.Configuration;
//import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freetime_software.marketcentral.view.ResetPasswordActivity;
import com.freetime_software.marketcentral.view.CreateAccountActivity;
import com.freetime_software.marketcentral.view.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


@SuppressWarnings("FieldCanBeLocal")
public class MainActivityLogin extends AppCompatActivity {

    private static final String TAG = "MainActivityLogin";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private Button btnLogin; //btnSignIn
    private TextView edtLostPassword;

    private TextInputEditText tedtUsername; //tedtEmail
    private TextInputEditText tedtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Firebase SignIn
        btnLogin          = findViewById(R.id.btnLogin); //btnSignIn
        edtLostPassword   = findViewById(R.id.edtLostPassword);
        tedtUsername      = findViewById(R.id.tedtUsername); //tedtEmail
        tedtPassword      = findViewById(R.id.tedtPassword);

        initialize();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((tedtUsername.getText() != null) && (tedtPassword.getText() != null)) {
                    if (tedtUsername.getText().toString().equals("")) {
                        Toast.makeText(MainActivityLogin.this, R.string.Empty_user_field, Toast.LENGTH_SHORT).show();
                    } else if (tedtPassword.getText().toString().equals("")) {
                        Toast.makeText(MainActivityLogin.this, R.string.Empty_password_field, Toast.LENGTH_SHORT).show();
                    } else {
                        login(tedtUsername.getText().toString(), tedtPassword.getText().toString());
                    }
                }
            }
        });

    }

    private void initialize() {
        // Firebase Account initialize
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.w(TAG, "onAuthStateChanged - signed_in " + firebaseUser.getUid());
                    Log.w(TAG, "onAuthStateChanged - signed_in " + firebaseUser.getEmail());
                    // automatic sign in if user not sign out
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(MainActivityLogin.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };
    }

    // Firebase Login (Sign In)
    private void login(String username, String password){
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if ((firebaseAuth.getCurrentUser() != null) &&
                            (firebaseAuth.getCurrentUser().isEmailVerified())) {
                        Toast.makeText(MainActivityLogin.this, R.string.Auth_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivityLogin.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(MainActivityLogin.this, R.string.email_not_verified, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivityLogin.this, R.string.Auth_not_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public void goCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void goResetPassword(View view) {
        // Also add corresponding changes on manifest file
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
        // Do not Destroy this activity after switching. (up button needs it)
        // finish();
    }
}
