package com.snaptiongame.snaption.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.HomeAppCompatActivity;

/**
 * Created by austinrobarts on 3/9/17.
 */
public class ProfileActivity extends HomeAppCompatActivity {

    private ProfileFragment fragment;
    private boolean addFriendIsVisible = false;

    public interface ProfileActivityCreator {
        void create(String userId);
    }

    public static ProfileActivityCreator getProfileActivityCreator(final Context context) {
        return new ProfileActivityCreator() {
            @Override
            public void create(String userId) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(ProfileFragment.USER_ID_ARG, userId);
                context.startActivity(intent);
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //go to the user's profile
        fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ProfileFragment.USER_ID_ARG, getIntent().getStringExtra(ProfileFragment.USER_ID_ARG));
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_fragment, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        MenuItem addFriendItem = menu.findItem(R.id.profile_add_friend);
        addFriendItem.setVisible(addFriendIsVisible);
        addFriendItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fragment.addFriend();
                return true;
            }
        });
        return true;
    }

    public void setAddFriendVisible(boolean visible) {
        addFriendIsVisible = visible;
        invalidateOptionsMenu();
    }
}
