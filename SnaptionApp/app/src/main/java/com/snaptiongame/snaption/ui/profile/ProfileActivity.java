package com.snaptiongame.snaption.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.HomeAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

/**
 * Created by austinrobarts on 3/9/17.
 */
public class ProfileActivity extends HomeAppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

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
        //set toolbar up
        ButterKnife.bind(this);
        setupToolbar(toolbar);

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
        //add menu to toolbar
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem addFriendItem = menu.findItem(R.id.profile_add_friend);
        addFriendItem.setVisible(addFriendIsVisible);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if user clicked add friend button
        if (item.getItemId() == R.id.profile_add_friend) {
            addFriend();
            return true;
        }
        //else check to see if its back button
        return super.onOptionsItemSelected(item);
    }

    public void setAddFriendVisible(boolean visible) {
        addFriendIsVisible = visible;
        invalidateOptionsMenu();
    }

    public void addFriend() {
        final User thisUser = fragment.getUser();
        String userId = FirebaseUserResourceManager.getUserId();
        if (userId != null && thisUser != null && !thisUser.getId().equals(userId)) {
            FirebaseUserResourceManager.getUserMetadataById(userId, new ResourceListener<UserMetadata>() {
                @Override
                public void onData(UserMetadata user) {
                    Uploader uploader = new FirebaseUploader();
                    uploader.addFriend(user, new Friend(thisUser.getId(), thisUser.getDisplayName(), thisUser.getEmail(), thisUser.getFacebookId()), new Uploader.UploadListener() {
                        @Override
                        public void onComplete() {
                            //show friend added toast
                            String addedFriend = String.format(getResources().getString(R.string.added_friend), thisUser.getDisplayName());
                            Toast.makeText(ProfileActivity.this, addedFriend, Toast.LENGTH_SHORT).show();
                            //hide add friend button
                            setAddFriendVisible(false);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            //show friend failed to add toast
                            String failedFriend = String.format(getResources().getString(R.string.problem_adding_friend), thisUser.getDisplayName());
                            Toast.makeText(ProfileActivity.this, failedFriend, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public Class getDataType() {
                    return UserMetadata.class;
                }
            });
        }
    }
}
