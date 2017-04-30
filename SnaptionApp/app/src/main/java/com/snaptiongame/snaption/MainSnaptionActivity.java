package com.snaptiongame.snaption;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.DeepLinkGetter;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.GameType;
import com.snaptiongame.snaption.servercalls.LoginManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.HomeAppCompatActivity;
import com.snaptiongame.snaption.ui.MainFabBehavior;
import com.snaptiongame.snaption.ui.friends.AddInviteFriendsActivity;
import com.snaptiongame.snaption.ui.friends.FriendsFragment;
import com.snaptiongame.snaption.ui.login.LoginDialog;
import com.snaptiongame.snaption.ui.new_game.CreateGameActivity;
import com.snaptiongame.snaption.ui.profile.ProfileFragment;
import com.snaptiongame.snaption.ui.wall.WallFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaption.Constants.GOOGLE_LOGIN_RC;

public class MainSnaptionActivity extends HomeAppCompatActivity {
    private static final String survey_url = "https://docs.google.com/forms/d/e/1FAIpQLSerSw6piYc20yi64SVjM48n7MklEFrg4Nk-oS5oRhlz_uxxRA/viewform";
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
    protected View navDrawerPhotoContainer;
    protected ImageView navDrawerPhoto;
    protected TextView navDrawerName;
    protected TextView navDrawerEmail;

    private LoginManager loginManager;
    public LoginDialog loginDialog;
    private ActionBarDrawerToggle mDrawerToggle;
    private UserMetadata currentUser;
    private int currentNavDrawerMenuId;
    private int currentBottomNavMenuId;
    // Used for keeping track of if this Activity is paused -- needed so logging in from
    // other screens will not trigger an attempted UI update while this activity is gone.
    private boolean isPaused;

    private NavigationView.OnNavigationItemSelectedListener mNavListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        // onNavigationItemSelected gets called when an item in the navigation drawer is selected
        // any replacing of fragments should be handled here
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            return switchFragments(item.getItemId());
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        // onNavigationItemSelected gets called when an item in the bottom navigation bar is selected
        // any replacing of fragments should be handled here
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return switchFragments(item.getItemId());
        }
    };

    /**
     * If you know the id of the fragment to switch to, call this method with it.
     * @param selectedItemId Id of the item to switch to, used to determine which fragment to load
     * @return true always
     */
    public boolean switchFragments(int selectedItemId) {
        // if the selected item is different than the currently selected item, replace the fragment
        if (selectedItemId != currentNavDrawerMenuId && selectedItemId != currentBottomNavMenuId) {
            Fragment newFragment = null;
            switch (selectedItemId) {
                case R.id.feedback_item:
                    //provide survey for bug reporting and feature requests/reviews
                    Uri uri = Uri.parse(survey_url);
                    //go to the website for google form
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    break;
                case R.id.wall_item:
                    MenuItem bottomNavMenuItem = bottomNavigationView.getMenu().findItem(R.id.my_feed_item);
                    if (bottomNavMenuItem == null) {
                        bottomNavMenuItem = bottomNavigationView.getMenu().findItem(R.id.popular_item);
                    }
                    currentNavDrawerMenuId = selectedItemId;
                    bottomNavMenuItem.setChecked(true);
                    bottomNavigationListener.onNavigationItemSelected(bottomNavMenuItem);
                    break;
                case R.id.profile_item:
                    newFragment = new ProfileFragment();
                    Bundle args = new Bundle();
                    args.putString(ProfileFragment.USER_ID_ARG, FirebaseUserResourceManager.getUserId());
                    newFragment.setArguments(args);
                    currentNavDrawerMenuId = selectedItemId;
                    currentBottomNavMenuId = 0;
                    break;
                case R.id.friends_item:
                    newFragment = new FriendsFragment();
                    currentNavDrawerMenuId = selectedItemId;
                    currentBottomNavMenuId = 0;
                    break;
                case R.id.log_option:
                    logInOutItemSelected();
                    break;
                case R.id.my_feed_item:
                    newFragment = WallFragment.newInstance(GameType.USER_JOINED_GAMES);
                    currentBottomNavMenuId = selectedItemId;
                    break;
                case R.id.discover_item:
                    newFragment =  WallFragment.newInstance(GameType.UNPOPULAR_PUBLIC_GAMES);
                    currentBottomNavMenuId = selectedItemId;
                    break;
                case R.id.popular_item:
                    newFragment =  WallFragment.newInstance(GameType.TOP_PUBLIC_GAMES);
                    currentBottomNavMenuId = selectedItemId;
                    break;
            }
            replaceFragmentWithTransaction(newFragment);
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void replaceFragmentWithTransaction(Fragment newFragment) {
        if (newFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (currentBottomNavMenuId != 0) {
                ft.setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out);
            }
            ft.replace(R.id.fragment_container, newFragment).commit();
            updateFragmentViews();
        }
    }

    private void updateFragmentViews() {
        setToolbarCollapsible(currentNavDrawerMenuId != R.id.profile_item);
        // show the fab
        // I don't know why hide needs to be called first, but it doesn't work otherwise
        fab.hide();
        fab.show();
        // hide or show the bottom navigation view
        bottomNavigationView.setVisibility(currentNavDrawerMenuId == R.id.wall_item ?
                View.VISIBLE : View.GONE);
        fab.setImageResource(currentNavDrawerMenuId == R.id.profile_item ?
                R.drawable.ic_mode_edit_white_24dp : R.drawable.ic_add_white_24dp);
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

    private void logInOutItemSelected() {
        final MenuItem item = navigationView.getMenu().findItem(R.id.log_option);
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
                                    currentUser = null;
                                    loginManager.logOut();
                                    item.setTitle(getResources().getString(R.string.login));
                                }
                            }).setNegativeButton(getResources().getString(R.string.no),
                    null).create().show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPaused = false;
        FacebookSdk.sdkInitialize(getApplicationContext());

        // set layout and bind views
        setContentView(R.layout.activity_main_snaption);
        ButterKnife.bind(this);

        // toolbar and navigation drawer setup
        setupToolbar(toolbar);
        setupNavigationViews();

        currentNavDrawerMenuId = R.id.wall_item;

        //create loginDialog and LoginManager to manager user
        loginDialog = new LoginDialog(this);
        loginManager = new LoginManager(this, new FirebaseUploader(), new LoginManager.LoginListener() {
            @Override
            public void onLoginComplete() {
                if(!isPaused) {
                    updateNavigationViews();
                }
            }
            @Override
            public void onLogoutComplete() {
                switchFragments(R.id.wall_item);
                updateNavigationViews();
            }
        }, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                //login was a success
                if(!isPaused) {
                    loginDialog.showPostLogDialog(getResources().getString(R.string.login_success));
                }
            }
            @Override
            public void onError() {
                //login was a failure
                if(!isPaused) {
                    loginDialog.showPostLogDialog(getResources().getString(R.string.login_failure));
                }
            }
        }, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                //logout was a success
                if(!isPaused) {
                    loginDialog.showPostLogDialog(getResources().getString(R.string.logout_success));
                }
            }

            @Override
            public void onError() {
                //logout was a failure
                if(!isPaused) {
                    loginDialog.showPostLogDialog(getResources().getString(R.string.logout_failure));
                }
            }
        });
        loginDialog.setLoginManager(loginManager);
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).setBehavior(new MainFabBehavior(this, null));

        DeepLinkGetter.checkIfDeepLink(this);
    }

    private void setupNavigationViews() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_nav_drawer, R.string.close_nav_drawer) {};
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(mNavListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        // navigation drawer view setup
        final View navigationHeaderView = navigationView.getHeaderView(0);
        navDrawerPhotoContainer = ButterKnife.findById(navigationHeaderView, R.id.photo_container);
        navDrawerPhoto = ButterKnife.findById(navigationHeaderView, R.id.user_photo);
        navDrawerName = ButterKnife.findById(navigationHeaderView, R.id.user_name);
        navDrawerEmail = ButterKnife.findById(navigationHeaderView, R.id.user_email);
    }

    private void updateNavigationViews() {
        String id = FirebaseUserResourceManager.getUserId();
        if (id != null) {
            if (currentUser == null) {
                //retrieve information from User table
                FirebaseUserResourceManager.getUserMetadataById(id, new ResourceListener<UserMetadata>() {
                            @Override
                            public void onData(UserMetadata user) {
                                currentUser = user;
                                if (user != null) {
                                    addUserInfoToNavDrawer(user);
                                } else {
                                    removeUserInfoFromNavDrawer();
                                }
                            }

                            @Override
                            public Class getDataType() {
                                return UserMetadata.class;
                            }
                        });
            }
        } else {
            removeUserInfoFromNavDrawer();
        }
    }

    private void addUserInfoToNavDrawer(UserMetadata user) {
        //load user data into views
        navDrawerName.setText(user.getDisplayName());
        navDrawerEmail.setText(user.getEmail());
        FirebaseResourceManager.loadImageIntoView(user.getImagePath(), navDrawerPhoto);
        //set user info to visible now they are logged in
        navDrawerPhotoContainer.setVisibility(View.VISIBLE);
        navDrawerName.setVisibility(View.VISIBLE);
        navDrawerEmail.setVisibility(View.VISIBLE);
        //set logged in only options to visible
        navigationView.getMenu().findItem(R.id.profile_item).setVisible(true);
        navigationView.getMenu().findItem(R.id.friends_item).setVisible(true);
        //change log option item drawable to logout drawable
        navigationView.getMenu().findItem(R.id.log_option).setIcon(ContextCompat
                .getDrawable(this, R.drawable.logout));
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
        navDrawerEmail.setVisibility(View.GONE);
        navDrawerName.setVisibility(View.GONE);
        navDrawerPhotoContainer.setVisibility(View.GONE);
        //set logged in only options to hidden
        navigationView.getMenu().findItem(R.id.profile_item).setVisible(false);
        navigationView.getMenu().findItem(R.id.friends_item).setVisible(false);
        //change log option item drawable to login drawable
        navigationView.getMenu().findItem(R.id.log_option).setIcon(ContextCompat
                .getDrawable(this, R.drawable.login));
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
            if (FirebaseUserResourceManager.getUserId() != null) {
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
        isPaused = false;
        updateNavigationViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
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
