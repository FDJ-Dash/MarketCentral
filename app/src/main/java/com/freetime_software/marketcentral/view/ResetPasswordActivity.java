package com.freetime_software.marketcentral.view;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.freetime_software.marketcentral.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("FieldCanBeLocal")
public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private Button btnResetPswEmail;
    private TextInputEditText tedtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        showToolbar(getResources().getString(R.string.toolbar_title_forgotpassword),true);

        tedtEmail           = findViewById(R.id.emailRP); // username
        btnResetPswEmail    = findViewById(R.id.btnResetPswRP);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Log.w(TAG, "onAuthStateChanged - User logged " + firebaseUser.getEmail());
//                    Crashlytics.log(Log.WARN, TAG, "User logged " + firebaseUser.getEmail());
                }else {
                    Log.w(TAG, "onAuthStateChanged - User not logged");
//                    Crashlytics.log(Log.WARN, TAG, "User not logged ");
                }
            }
        };

        btnResetPswEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tedtEmail.getText() != null) {
                    if (tedtEmail.getText().toString().equals("")) {
                       Toast.makeText(ResetPasswordActivity.this, R.string.Empty_email_field, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "Before resetPasswordEmail");
                        resetPasswordEmail(tedtEmail.getText().toString());
                    }
                }
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void showToolbar(String tittle, @SuppressWarnings("SameParameterValue") boolean upButton) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(tittle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        }
    }

    private void resetPasswordEmail(String email){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(ResetPasswordActivity.this, R.string.psw_reset_email_sent, Toast.LENGTH_SHORT).show();
                        }else {
                            //invalid or not found email
                            Toast.makeText(ResetPasswordActivity.this, R.string.InvldEmail, Toast.LENGTH_SHORT).show();
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
}
