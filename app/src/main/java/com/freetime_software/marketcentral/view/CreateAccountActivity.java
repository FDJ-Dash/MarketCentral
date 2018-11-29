package com.freetime_software.marketcentral.view;

import android.annotation.SuppressLint;
//import android.net.Uri;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.UserProfileChangeRequest;

@SuppressWarnings("FieldCanBeLocal")
public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private Button btnJoinUs;
    private TextInputEditText tedtEmail, tedtPassword, tedtConfPassword;
//    private TextInputEditText tedtName, tedtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        showToolbar(getResources().getString(R.string.toolbar_title_createaccount),true);

        btnJoinUs   = findViewById(R.id.joinUs);
        tedtEmail    = findViewById(R.id.email);
//        tedtName  = findViewById(R.id.name);
//        tedtUser = findViewById(R.id.user);
        tedtPassword = findViewById(R.id.password_createaccount);
        tedtConfPassword = findViewById(R.id.confirmPassword);

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

        btnJoinUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createAccount();
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

    private void createAccount() {
        if ((tedtEmail.getText() != null) &&
            (tedtPassword.getText() != null) &&
            (tedtConfPassword.getText() != null)) {

            String email = tedtEmail.getText().toString();
//            String name = tedtName.getText().toString();
//            String user = tedtUser.getText().toString();
            String password = tedtPassword.getText().toString();
            String confPassword = tedtConfPassword.getText().toString();

            if (tedtEmail.getText().toString().equals("")) {
                Toast.makeText(CreateAccountActivity.this, R.string.Empty_email_field, Toast.LENGTH_SHORT).show();
            } else if (tedtPassword.getText().toString().equals("")) {
                Toast.makeText(CreateAccountActivity.this, R.string.Empty_password_field, Toast.LENGTH_SHORT).show();
            } else if (password.equals(confPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // send email verification
                                if (firebaseAuth.getCurrentUser() != null) {
                                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CreateAccountActivity.this,
                                                                getString(R.string.email_verif_sent_to)+ " " + user.getEmail(), Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(CreateAccountActivity.this, R.string.email_verification_not_sent, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                //invalid or existing email or weak password
                                Toast.makeText(CreateAccountActivity.this, R.string.invldEmailOrWeakPsw, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            } else {
                // Password doesn't match
                Toast.makeText(CreateAccountActivity.this, R.string.conf_password, Toast.LENGTH_SHORT).show();
            }
        }
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
