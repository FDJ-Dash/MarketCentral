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

import com.facebook.login.LoginManager;
import com.freetime_software.marketcentral.MainActivityLogin;
import com.freetime_software.marketcentral.R;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HomeActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Google Sign Out
    private GoogleApiClient googleApiClient;

    ///////////////////////////
    private TextView tvUserDetail;
    private Button btnLogout;
    private ImageView imvPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUserDetail = findViewById(R.id.tvUserDetail);
        btnLogout   = findViewById(R.id.btnLogout);
        imvPhoto     = findViewById(R.id.imvPhoto);

        initialize();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                Toast.makeText(HomeActivity.this, R.string.SignOut, Toast.LENGTH_SHORT).show();
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
                    Picasso.get().load(firebaseUser.getPhotoUrl()).into(imvPhoto);
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };

        //Google Account initialize
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void logout() {
        // Firebase Logout (Sign Out)
        firebaseAuth.signOut();

        // Google Sign Out
        if (Auth.GoogleSignInApi != null) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.w(TAG, "Google Sign Out Successful");
                    } else {
                        Log.w(TAG, "Google Sign Out Failed");
                    }
                }
            });
        }

        // Facebook Sign Out
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
