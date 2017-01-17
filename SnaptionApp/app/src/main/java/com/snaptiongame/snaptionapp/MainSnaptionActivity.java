package com.snaptiongame.snaptionapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUpload;
import com.snaptiongame.snaptionapp.ui.wall.WallFragment;

public class MainSnaptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main_snaption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        WallFragment frag = new WallFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                frag, WallFragment.class.getSimpleName()).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Just for testing purposes. Remove this later
        // Below works and sets value when running the app. Doesn't work when is run via testing
        // for some reason. Further investigation required.

        FirebaseUpload.uploadString("test/test2", "whateva");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_snaption, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_login) {
            //create pop up for login Facebook or Google+
            LoginDialog logDialog = new LoginDialog(this);
            logDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
