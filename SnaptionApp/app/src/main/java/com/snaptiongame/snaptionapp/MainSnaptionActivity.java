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
import android.support.v7.app.AlertDialog;
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
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.DeepLinkGetter;
import com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinkCreator;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.LoginManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.ScrollViewHider;
import com.snaptiongame.snaptionapp.ui.friends.AddInviteFriendsActivity;
import com.snaptiongame.snaptionapp.ui.friends.FriendsFragment;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;
import com.snaptiongame.snaptionapp.ui.profile.ProfileFragment;
import com.snaptiongame.snaptionapp.ui.wall.WallFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaptionapp.servercalls.LoginManager.GOOGLE_LOGIN_RC;
import static com.snaptiongame.snaptionapp.ui.games.GameActivity.USE_GAME_ID;

public class MainSnaptionActivity extends AppCompatActivity {
    private LoginManager loginManager;
    public LoginDialog loginDialog;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;

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
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            int selectedItemId = item.getItemId();
            // if the selected item is different than the currently selected item, replace the fragment
            if (selectedItemId != currentFragmentMenuItemId) {
                switch (selectedItemId) {
                    case R.id.wall_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new WallFragment()).commit();
                        break;
                    case R.id.profile_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                        break;
                    case R.id.friends_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new FriendsFragment()).commit();
                        break;
                    case R.id.log_option:
                        //check if we are logging in or out based on item text
                        if (item.getTitle().equals(getResources().getString(R.string.login))) {
                            //display dialog
                            loginDialog.show();
                        }
                        else {
                            new AlertDialog.Builder(MainSnaptionActivity.this, 0)
                                    .setMessage(getResources().getString(R.string.logout_prompt))
                                    .setPositiveButton(getResources().getString(R.string.yes),
                                            new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            loginManager.logOut();
                                            item.setTitle(getResources().getString(R.string.login));
                                        }
                                    }).setNegativeButton(getResources().getString(R.string.no),
                                            null).create().show();

                        }
                        //because this is not a fragment we cannot set currentFragment to it so we reset it to last fragment
                        selectedItemId = currentFragmentMenuItemId;
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
        setupNavigationView();

        // wall fragment instantiation
        currentFragmentMenuItemId = R.id.wall_item;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                new WallFragment()).commit();
        //create loginDialog and LoginManager to manager user
        loginDialog = new LoginDialog(this);
        loginManager = new LoginManager(this, new FirebaseUploader(), new LoginManager.LoginListener() {
            @Override
            public void onLoginComplete() {
                setupNavigationView();
            }
        }, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                //login was a success
                loginDialog.showPostLogDialog(getResources().getString(R.string.login_success));
            }
            @Override
            public void onError() {
                //login was a failure
                loginDialog.showPostLogDialog(getResources().getString(R.string.login_failure));
            }
        }, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                //logout was a success
                loginDialog.showPostLogDialog(getResources().getString(R.string.logout_success));
            }

            @Override
            public void onError() {
                //logout was a failure
                loginDialog.showPostLogDialog(getResources().getString(R.string.logout_failure));
            }
        });
        loginDialog.setLoginManager(loginManager);

        DeepLinkGetter.checkIfDeepLink(this);
    }

    private void setupNavigationView() {
        FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
        // navigation drawer view setup
        final View navigationHeaderView = navigationView.getHeaderView(0);
        navDrawerPhoto = (ImageView) navigationHeaderView.findViewById(R.id.user_photo);
        navDrawerName = (TextView) navigationHeaderView.findViewById(R.id.user_name);
        navDrawerEmail = (TextView) navigationHeaderView.findViewById(R.id.user_email);

        if (FirebaseResourceManager.getUserPath() != null) {
            //retrieve information from User table
            firebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(), new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    if (user != null) {
                        addUserInfoToNavDrawer(user);
                    } else {
                        removeUserInfoFromNavDrawer();
                    }
                }
                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        } else {
            removeUserInfoFromNavDrawer();
        }
    }

    private void addUserInfoToNavDrawer(User user) {
        //load user data into views
        navDrawerName.setText(user.getDisplayName());
        navDrawerEmail.setText(user.getEmail());
        FirebaseResourceManager.loadImageIntoView(user.getImagePath(), navDrawerPhoto);
        //set user info to visible now they are logged in
        navDrawerPhoto.setVisibility(View.VISIBLE);
        navDrawerName.setVisibility(View.VISIBLE);
        navDrawerEmail.setVisibility(View.VISIBLE);
        //set logged in only options to visible
        navigationView.getMenu().findItem(R.id.profile_item).setVisible(true);
        navigationView.getMenu().findItem(R.id.friends_item).setVisible(true);
        //set drawer item to say log out
        navigationView.getMenu().findItem(R.id.log_option).setTitle(getResources().getString(R.string.logout));

    }

    private void removeUserInfoFromNavDrawer() {
        //hide elements because user is logged out
        navDrawerPhoto.setVisibility(View.INVISIBLE);
        navDrawerName.setVisibility(View.INVISIBLE);
        navDrawerEmail.setVisibility(View.INVISIBLE);
        //set logged in only options to hidden
        navigationView.getMenu().findItem(R.id.profile_item).setVisible(false);
        navigationView.getMenu().findItem(R.id.friends_item).setVisible(false);
        //set drawer item to say login
        navigationView.getMenu().findItem(R.id.log_option).setTitle(getResources().getString(R.string.login));
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        if (currentFragmentMenuItemId == R.id.wall_item) {
            if (FirebaseResourceManager.getUserId() != null) {
                Intent intent = new Intent(this, CreateGameActivity.class);
                startActivity(intent);
            }
            else {
                loginDialog.show();
            }

        }
        else if (currentFragmentMenuItemId == R.id.friends_item) {
            Intent intent = new Intent(this, AddInviteFriendsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationView();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
            loginManager.handleGoogleLoginResult(result);
        }
        //if returning from facebook login attempt
        else {
            loginManager.handleFacebookLoginResult(requestCode, resultCode, data);
        }
    }
}
