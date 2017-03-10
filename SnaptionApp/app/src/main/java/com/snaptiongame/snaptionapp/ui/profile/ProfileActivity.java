package com.snaptiongame.snaptionapp.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.ui.HomeAppCompatActivity;

/**
 * Created by austinrobarts on 3/9/17.
 */

public class ProfileActivity extends HomeAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //go to the user's profile
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", getIntent().getStringExtra("userId"));
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_fragment, fragment)
                .commit();



    }
}
