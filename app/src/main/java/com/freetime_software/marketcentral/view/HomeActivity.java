package com.freetime_software.marketcentral.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freetime_software.marketcentral.MainActivityLogin;
import com.freetime_software.marketcentral.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView tvUserDetail;
    private Button btnLogout;
//    private ImageView imvPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUserDetail = findViewById(R.id.tvUserDetail);
        btnLogout   = findViewById(R.id.btnLogout);
//        imvPhoto     = findViewById(R.id.imvPhoto);

        initialize();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                // Go back to login activity
                Intent intent = new Intent(HomeActivity.this, MainActivityLogin.class);
                startActivity(intent);
                finish();
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
                    tvUserDetail.setText("IDUser: " + firebaseUser.getUid() + " Email: " + firebaseUser.getEmail());
//                    Picasso.get().load(firebaseUser.getPhotoUrl()).into(imvPhoto);
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };
    }

    private void logout() {
        // Firebase Logout (Sign Out)
        firebaseAuth.signOut();
        Toast.makeText(HomeActivity.this, R.string.FIR_SignOut, Toast.LENGTH_SHORT).show();
//        // Google Sign Out
//        if (Auth.GoogleSignInApi != null) {
//            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
//                @Override
//                public void onResult(@NonNull Status status) {
//                    if (status.isSuccess()) {
//                        Toast.makeText(WelcomeActivity.this, "Google Sign Out Successful", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(WelcomeActivity.this, "Google Sign Out Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
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
