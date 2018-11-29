package com.freetime_software.marketcentral.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.freetime_software.marketcentral.MainActivityLogin;
import com.freetime_software.marketcentral.R;
import com.freetime_software.marketcentral.view.fragment.AboutFragment;
import com.freetime_software.marketcentral.view.fragment.HomeFragment;
import com.freetime_software.marketcentral.view.fragment.MessageFragment;
import com.freetime_software.marketcentral.view.fragment.ProfileFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class FragmentContainerActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "FragmentContainerActiv";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Google Sign Out
    private GoogleApiClient googleApiClient;

    // Drawer
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    // Start fragment navigation bar
    private final HomeFragment homeFragment = new HomeFragment();
    private final ProfileFragment profileFragment  = new ProfileFragment();
    private final MessageFragment messageFragment = new MessageFragment();
    private final AboutFragment aboutFragment = new AboutFragment();

    // User details
    private TextView tvUserEmail;
    private ImageView imvPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        toolbar = findViewById(R.id.toolbar);
        // Set toolbar title on each fragment
        showToolbar(getResources().getString(R.string.toolbar_title_home), false);

        // drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // drawer header
        View headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        imvPhoto     = headerView.findViewById(R.id.imvPhoto);

        initialize();

        //set HomeFragment as Default on first load (nav_view)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .commit();
        }

        navigationView.setCheckedItem(R.id.nav_home);

        nav_view();

    } // End onCreate()

    @SuppressLint("RestrictedApi")
    private void showToolbar(String title, @SuppressWarnings("SameParameterValue") boolean upButton) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void initialize() {
        // Firebase Account initialize
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // Displays user data
                    tvUserEmail.setText(firebaseUser.getEmail());
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
    } // End initialize()


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

    private void nav_view() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        addFragment(homeFragment);
                        break;
                    case R.id.nav_profile:
                        addFragment(profileFragment);
                        break;
                    case R.id.nav_message:
                        addFragment(messageFragment);
                        break;
                    case R.id.nav_share:
                        Toast.makeText(FragmentContainerActivity.this, "Share", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_send:
                        Toast.makeText(FragmentContainerActivity.this, "Send", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_about:
                        addFragment(aboutFragment);
                        break;
                    case R.id.nav_logout:
                        logout();
                        // Go back to login activity
                        Toast.makeText(FragmentContainerActivity.this, R.string.SignOut, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FragmentContainerActivity.this, MainActivityLogin.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            //Set fragment
            private void addFragment(Fragment fragment) {
                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    } // End nav_view()

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
