package com.snaptiongame.snaptionapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.GameType;
import com.snaptiongame.snaptionapp.servercalls.LoginManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.friends.AddInviteFriendsActivity;
import com.snaptiongame.snaptionapp.ui.friends.FriendsFragment;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;
import com.snaptiongame.snaptionapp.ui.new_game.CreateGameActivity;
import com.snaptiongame.snaptionapp.ui.profile.ProfileFragment;
import com.snaptiongame.snaptionapp.ui.wall.WallFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaptionapp.Constants.GOOGLE_LOGIN_RC;

public class MainSnaptionActivity extends AppCompatActivity {
    private LoginManager loginManager;
    public LoginDialog loginDialog;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;
    @BindView(R.id.bottom_nav)
    protected BottomNavigationView bottomNavigationView;
    @BindView(R.id.fab)
    protected FloatingActionButton fab;
    protected ImageView navDrawerPhoto;
    protected TextView navDrawerName;
    protected TextView navDrawerEmail;


    private int currentNavDrawerMenuId;
    private int currentBottomNavMenuId;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView.OnNavigationItemSelectedListener mNavListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        // onNavigationItemSelected gets called when an item in the navigation drawer is selected
        // any replacing of fragments should be handled here
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            return switchFragments(item);
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        // onNavigationItemSelected gets called when an item in the bottom navigation bar is selected
        // any replacing of fragments should be handled here
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return switchFragments(item);
        }
    };

    private boolean switchFragments(final MenuItem item) {
        int selectedItemId = item.getItemId();
        // if the selected item is different than the currently selected item, replace the fragment
        if (selectedItemId != currentNavDrawerMenuId && selectedItemId != currentBottomNavMenuId) {
            boolean fabVisible = true;
            boolean bottomNavVisible = true;
            boolean fadeAnim = false;
            Fragment newFragment = null;
            switch (selectedItemId) {
                case R.id.wall_item:
                    MenuItem bottomNavMenuItem;
                    if (FirebaseResourceManager.getUserId() != null) {
                        bottomNavMenuItem = bottomNavigationView.getMenu().findItem(R.id.my_feed_item);
                    }
                    else {
                        bottomNavMenuItem = bottomNavigationView.getMenu().findItem(R.id.popular_item);
                    }
                    currentNavDrawerMenuId = selectedItemId;
                    bottomNavMenuItem.setChecked(true);
                    setToolbarCollapsible(true);
                    fab.setImageResource(R.drawable.ic_add_white_24dp);
                    bottomNavigationListener.onNavigationItemSelected(bottomNavMenuItem);
                    break;
                case R.id.profile_item:
                    newFragment = new ProfileFragment();
                    bottomNavVisible = false;
                    currentNavDrawerMenuId = selectedItemId;
                    currentBottomNavMenuId = 0;
                    setToolbarCollapsible(false);
                    fab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
                    break;
                case R.id.friends_item:
                    newFragment = new FriendsFragment();
                    bottomNavVisible = false;
                    currentNavDrawerMenuId = selectedItemId;
                    currentBottomNavMenuId = 0;
                    setToolbarCollapsible(true);
                    fab.setImageResource(R.drawable.ic_add_white_24dp);
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
                    break;
                case R.id.my_feed_item:
                    fadeAnim = true;
                    newFragment = WallFragment.newInstance(GameType.PRIVATE_GAMES);
                    currentBottomNavMenuId = selectedItemId;
                    break;
                case R.id.discover_item:
                    fadeAnim = true;
                    newFragment =  WallFragment.newInstance(GameType.MIXED_GAMES);
                    currentBottomNavMenuId = selectedItemId;
                    break;
                case R.id.popular_item:
                    fadeAnim = true;
                    newFragment =  WallFragment.newInstance(GameType.PUBLIC_GAMES);
                    currentBottomNavMenuId = selectedItemId;
                    break;
            }
            if (newFragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (fadeAnim) {
                    ft.setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out);
                }
                ft.replace(R.id.fragment_container, newFragment);
                ft.commit();
                updateFragmentViews(fabVisible, bottomNavVisible);
            }
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void updateFragmentViews(boolean fabVisible, boolean bottomNavVisible) {
        // hide or show the fab
        fab.setVisibility(fabVisible ? View.VISIBLE : View.GONE);
        // hide or show the bottom navigation view
        bottomNavigationView.setVisibility(bottomNavVisible ? View.VISIBLE : View.GONE);
        // change the margin of the fab depending on if the bottom navigation view is shown
        Resources res = getResources();
        int fabEndMargin = res.getDimensionPixelSize(R.dimen.fab_margin);
        int fabBottomNavBottomMargin = fabEndMargin +
                res.getDimensionPixelSize(R.dimen.wall_bottom_navigation_height);
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).setMargins(0, 0, fabEndMargin,
                bottomNavVisible ? fabBottomNavBottomMargin : 0);
    }

    private void setToolbarCollapsible(boolean collapsible) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        if(collapsible) {
            params.setScrollFlags(
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        } else {
            params.setScrollFlags(0);
        }
    }

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
        setupNavigationViews();
        currentNavDrawerMenuId = R.id.wall_item;

        //create loginDialog and LoginManager to manager user
        loginDialog = new LoginDialog(this);
        loginManager = new LoginManager(this, new FirebaseUploader(), new LoginManager.LoginListener() {
            @Override
            public void onLoginComplete() {
                setupNavigationViews();
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

    private void setupNavigationViews() {
        navigationView.setNavigationItemSelectedListener(mNavListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
        // navigation drawer view setup
        final View navigationHeaderView = navigationView.getHeaderView(0);
        navDrawerPhoto = (ImageView) navigationHeaderView.findViewById(R.id.user_photo);
        navDrawerName = (TextView) navigationHeaderView.findViewById(R.id.user_name);
        navDrawerEmail = (TextView) navigationHeaderView.findViewById(R.id.user_email);

        if (FirebaseResourceManager.getUserPath() != null) {
            //retrieve information from User table
            FirebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(),
                    new ResourceListener<User>() {
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
        //add the my feed menu item if it is not already there
        MenuItem myFeedItem = bottomNavigationView.getMenu().findItem(R.id.my_feed_item);
        if (myFeedItem == null) {
            myFeedItem = bottomNavigationView.getMenu().add(Menu.NONE, R.id.my_feed_item, 0,
                    getResources().getString(R.string.my_feed));
            myFeedItem.setIcon(R.drawable.ic_person_pin_color_24dp);
        }
        //if on the wall, update the menu item
        if (currentNavDrawerMenuId == R.id.wall_item) {
            currentNavDrawerMenuId = -1;
            mNavListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.wall_item));
        }
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
        //remove the my feed menu item
        bottomNavigationView.getMenu().removeItem(R.id.my_feed_item);
        //if on the wall, update the menu item
        if (currentNavDrawerMenuId == R.id.wall_item) {
            currentNavDrawerMenuId = -1;
            mNavListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.wall_item));
        }
        //set drawer item to say login
        navigationView.getMenu().findItem(R.id.log_option).setTitle(getResources().getString(R.string.login));
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        if (currentNavDrawerMenuId == R.id.wall_item) {
            if (FirebaseResourceManager.getUserId() != null) {
                Intent intent = new Intent(this, CreateGameActivity.class);
                startActivity(intent);
            }
            else {
                loginDialog.show();
            }

        }
        else if (currentNavDrawerMenuId == R.id.friends_item) {
            Intent intent = new Intent(this, AddInviteFriendsActivity.class);
            startActivity(intent);
        }
        else if (currentNavDrawerMenuId == R.id.profile_item) {
            ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            fragment.fabClicked(fab, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationViews();
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
