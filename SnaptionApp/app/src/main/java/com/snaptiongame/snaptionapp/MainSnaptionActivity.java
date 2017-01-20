package com.snaptiongame.snaptionapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.snaptiongame.snaptionapp.ui.wall.WallFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainSnaptionActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    protected NavigationView mNavigationView;

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
                        break;
                    case R.id.profile_item:
                        //TODO swap out current fragment with the profile fragment
                        break;
                }
                currentFragmentMenuItemId = selectedItemId;
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_snaption);
        ButterKnife.bind(this);

        // toolbar and navigation drawer setup
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open_nav_drawer, R.string.close_nav_drawer) {};
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(mNavListener);

        // wall fragment instantiation
        currentFragmentMenuItemId = R.id.wall_item;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                new WallFragment()).commit();
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
}
