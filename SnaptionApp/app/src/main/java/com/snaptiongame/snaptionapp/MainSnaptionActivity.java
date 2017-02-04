package com.snaptiongame.snaptionapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.profile.ProfileFragment;
import com.snaptiongame.snaptionapp.ui.wall.WallFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaptionapp.LoginManager.GOOGLE_LOGIN_RC;

public class MainSnaptionActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private LoginManager loginManager;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;
    @BindView(R.id.fab)
    protected FloatingActionButton fab;
    protected ImageView navDrawerPhoto;
    protected TextView navDrawerName;
    protected TextView navDrawerEmail;


    private int currentFragmentMenuItemId;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView.OnNavigationItemSelectedListener mNavListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        // onNavigationItemSelected gets called when an item in the navigation drawer is selected
        // any replacing of fragments should be handled here
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int selectedItemId = item.getItemId();
            // if the selected item is different than the currently selected item, replace the fragment
            if (selectedItemId != currentFragmentMenuItemId) {
                switch (selectedItemId) {
                    case R.id.wall_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new WallFragment()).commit();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.profile_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                        fab.setVisibility(View.INVISIBLE);
                        break;
                }
                currentFragmentMenuItemId = selectedItemId;
            }
            drawerLayout.closeDrawers();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main_snaption);

        ButterKnife.bind(this);
        // toolbar and navigation drawer setup
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_nav_drawer, R.string.close_nav_drawer) {};
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(mNavListener);

        // navigation drawer view setup
        View navigationHeaderView = navigationView.getHeaderView(0);
        navDrawerPhoto = (ImageView) navigationHeaderView.findViewById(R.id.user_photo);
        navDrawerName = (TextView) navigationHeaderView.findViewById(R.id.user_name);
        navDrawerEmail = (TextView) navigationHeaderView.findViewById(R.id.user_email);
        setupNavigationView();

        // wall fragment instantiation
        currentFragmentMenuItemId = R.id.wall_item;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                new WallFragment()).commit();
        loginManager = new LoginManager(this, new FirebaseUploader());
    }

    private void setupNavigationView() {
        FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
        //if the user is logged in
        if (FirebaseResourceManager.getUserPath() != null) {
            //retrieve information from User table
            firebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(), new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    navDrawerName.setText(user.getDisplayName());
                    navDrawerEmail.setText(user.getEmail());
                    FirebaseResourceManager.loadImageIntoView(user.getImagePath(), navDrawerPhoto);
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {

        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_snaption, menu);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Snackbar.make(findViewById(R.id.drawer_layout), loginManager.getStatus(),Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_login) {
            if (!loginManager.isLoggedIn()) {
                //create pop up for login Facebook or Google+
                LoginDialog logDialog = new LoginDialog(this, loginManager, new LoginDialog.LoginListener() {
                    @Override
                    public void onLoginComplete() {
                        setupNavigationView();
                    }
                });
                logDialog.setOnDismissListener(this);
                logDialog.show();
            }
            else {
                onDismiss(null);
            }
        }

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * This is called after returning from a login intent from either Facebook or Google
     * This initiates the connection with firebase after contacting Facebook or Google
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if returning from google login attempt
        if (requestCode == GOOGLE_LOGIN_RC) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                loginManager.handleGoogleLoginResult(result);
            }
        }
        //if returning from facebook login attempt
        else {
            loginManager.handleFacebookLoginResult(requestCode, resultCode, data);
        }
    }
}
