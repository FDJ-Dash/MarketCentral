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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("FieldCanBeLocal")
public class EmailChangeActivity extends AppCompatActivity {
    private static final String TAG = "EmailChangeActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Global variables email and password
    private String email = "", password = "";

    private TextInputEditText tedtEmail, tedtPassword, tedtNewEmail;
    private Button btnQuickLogin, btnChangeEmail, btnQuickLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);

        showToolbar(getResources().getString(R.string.toolbar_tittle_updatemail),true);

        tedtEmail = findViewById(R.id.emailEC2);
        tedtPassword = findViewById(R.id.passwordEC3);
        tedtNewEmail = findViewById(R.id.newEmailEC6);
        btnQuickLogin = findViewById(R.id.btnQuickLogginEC4);
        btnChangeEmail = findViewById(R.id.btnChangeEmailEC7);
        btnQuickLogout = findViewById(R.id.btnquickLogoutEC8);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Log.w(TAG, "onAuthStateChanged - User logged " + firebaseUser.getEmail());
                    btnChangeEmail.setEnabled(true);
                    btnQuickLogout.setEnabled(true);
                }else {
                    Log.w(TAG, "onAuthStateChanged - User not logged");
                    btnChangeEmail.setEnabled(false);
                    btnQuickLogout.setEnabled(false);
                }
            }
        };

        btnQuickLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickLogin();
            }
        });

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmail();
            }
        });

        btnQuickLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickLogout();
            }
        });
    } // end onCreate()

    @SuppressLint("RestrictedApi")
    private void showToolbar(String tittle, @SuppressWarnings("SameParameterValue") boolean upButton) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(tittle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        }
    }

    private void quickLogin() {
        if ((tedtEmail.getText() != null) && (tedtPassword.getText() != null)) {
            this.email = tedtEmail.getText().toString();
            this.password = tedtPassword.getText().toString();

            if (this.email.equals("")) {
                Toast.makeText(EmailChangeActivity.this, R.string.Empty_email_field, Toast.LENGTH_SHORT).show();
            } else if (this.password.equals("")) {
                Toast.makeText(EmailChangeActivity.this, R.string.Empty_password_field, Toast.LENGTH_SHORT).show();
            } else {
                // Firebase Login (Sign In)
                firebaseAuth.signInWithEmailAndPassword(this.email, this.password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                Log.d(TAG, "User authenticated.");
                                // mail may not have full access if its not verified though
                                Toast.makeText(EmailChangeActivity.this, "Authenticated with quick Login", Toast.LENGTH_SHORT).show();
                                btnChangeEmail.setEnabled(true);
                                btnQuickLogout.setEnabled(true);
                            }
                        } else {
                            // invalid password
                            Log.d(TAG, "invalid password - user not logged");
                            Toast.makeText(EmailChangeActivity.this, R.string.Auth_not_success, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void updateEmail() {
        if (tedtNewEmail.getText() != null) {
            String newEmail = tedtNewEmail.getText().toString();

            if (tedtNewEmail.getText().toString().equals("")) {
                Toast.makeText(EmailChangeActivity.this, R.string.Empty_email_field, Toast.LENGTH_SHORT).show();
            } else {
                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(this.email, this.password);

                // Prompt the user to re-provide their sign-in credentials
                if (firebaseAuth.getCurrentUser() != null) {
                    firebaseAuth.getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "User re-authenticated.");
                                }
                            });

                    // Update email
                    firebaseAuth.getCurrentUser().updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                        // send email verification
                                        if (firebaseAuth.getCurrentUser() != null) {
                                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                                            user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(EmailChangeActivity.this,
                                                                    getString(R.string.email_verif_sent_to)+ " " + user.getEmail(), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(EmailChangeActivity.this, R.string.email_verification_not_sent, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        }
//                                        Toast.makeText(EmailChangeActivity.this, "Email Address Updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EmailChangeActivity.this, "Invalid or Existent Email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // user not logged
                    Toast.makeText(EmailChangeActivity.this, R.string.Auth_not_success, Toast.LENGTH_SHORT).show();
                }

            }
        }
    } // end updateEmail()

    private void quickLogout() {
        firebaseAuth.signOut();
        Toast.makeText(EmailChangeActivity.this, R.string.SignOut, Toast.LENGTH_SHORT).show();
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
