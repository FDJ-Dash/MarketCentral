package com.freetime_software.marketcentral;

import android.content.Intent;
//import android.content.res.Configuration;
//import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.freetime_software.marketcentral.view.EmailChangeActivity;
import com.freetime_software.marketcentral.view.FragmentContainerActivity;
import com.freetime_software.marketcentral.view.ResetPasswordActivity;
import com.freetime_software.marketcentral.view.CreateAccountActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


@SuppressWarnings("FieldCanBeLocal")
public class MainActivityLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivityLogin";

    // Firebase SignIn
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private Button btnLogin;
    private TextInputEditText tedtEmail;
    private TextInputEditText tedtPassword;

    // Google SignIn
    private static final int SIGN_IN_GOOGLE_CODE = 1;
    private GoogleApiClient googleApiClient;
    private SignInButton btnSignInGoogle;


    // Facebook SignIn
    private CallbackManager callbackManager;
    private LoginButton btnSignInFacebook;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Firebase SignIn
        btnLogin          = findViewById(R.id.btnLogin);
        tedtEmail         = findViewById(R.id.tedtEmail);
        tedtPassword      = findViewById(R.id.tedtPassword);

        // Google SignIn
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);

        //Facebook SignIn (Callback manager gets the data when user is logged succesfully)
        callbackManager         = CallbackManager.Factory.create();
        btnSignInFacebook       = findViewById(R.id.btnSignInFacebook);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();

        initialize();

        // Firebase
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((tedtEmail.getText() != null) && (tedtPassword.getText() != null)) {
                    if (tedtEmail.getText().toString().equals("")) {
                        Toast.makeText(MainActivityLogin.this, R.string.Empty_email_field, Toast.LENGTH_SHORT).show();
                    } else if (tedtPassword.getText().toString().equals("")) {
                        Toast.makeText(MainActivityLogin.this, R.string.Empty_password_field, Toast.LENGTH_SHORT).show();
                    } else {
                        login(tedtEmail.getText().toString(), tedtPassword.getText().toString());
                    }
                }
            }
        });

        // Google
        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        //Facebook Account Listener
        btnSignInFacebook.setReadPermissions("email", "public_profile");
        btnSignInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG, "Facebook Login Success Token: " + loginResult.getAccessToken().getToken());
                signInFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Login Cancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Login Error: ");
                error.printStackTrace();
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
//                    Log.w(TAG, "onAuthStateChanged - signed_in " + firebaseUser.getUid());
                    Log.w(TAG, "onAuthStateChanged - signed_in " + firebaseUser.getEmail());
                    // automatic sign in if user not sign out
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        goHome();
                    }
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

        // Facebook Account initialize
        if (isLoggedIn) {
            goHome();
        }
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
                        goHome();

                    } else {
                        Toast.makeText(MainActivityLogin.this, R.string.email_not_verified, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // invalid password
                    Toast.makeText(MainActivityLogin.this, R.string.Auth_not_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Google and Facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_GOOGLE_CODE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // Manejamos el resultado con googleSignInResult o se lo pasamos por parametro a una
            // funcion propia para ser mas ordenados
            signInGoogleFirebase(googleSignInResult);
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Google Sign In
    private void signInGoogleFirebase(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            if (googleSignInResult.getSignInAccount() != null) {
                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivityLogin.this, R.string.googleAuthSuccess, Toast.LENGTH_SHORT).show();
                            goHome();
                        } else {
                            Toast.makeText(MainActivityLogin.this, R.string.googleAuthNotSuccess, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(MainActivityLogin.this, R.string.googleSignInNotSuccess, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Facebook Sign In
    private void signInFacebookFirebase(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivityLogin.this, R.string.facebookAuthSuccess, Toast.LENGTH_SHORT).show();
                    goHome();
                }else {
                    Toast.makeText(MainActivityLogin.this, R.string.facebookAuthNotSuccess, Toast.LENGTH_SHORT).show();
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

    private void goHome() {
        // Also add corresponding changes on manifest file
        Intent intent = new Intent(this, FragmentContainerActivity.class);
        startActivity(intent);
        finish();
    }

    public void goCreateAccount(View view) {
        // Also add corresponding changes on manifest file
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

    public void goUpdateEmail(View view) {
        // Also add corresponding changes on manifest file
        Intent intent = new Intent(this, EmailChangeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
