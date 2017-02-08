package com.snaptiongame.snaptionapp.ui.friends;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AddInviteFriendsActivity
 *  * displays the current User's potential Snaption friends. Potential friends include Facebook,
 *    Google+, and contact friends that have yet to be added to the current User's Snaption friend
 *    list.
 *  * allows the current User to send an app invite to potential friends not using Snaption
 *  * allows the current User to send a friend request to potential friends using Snaption
 *
 * @author Brittany Berlanga
 */
public class AddInviteFriendsActivity extends AppCompatActivity {
    private FriendAdapter friendAdapter;

    @BindView(R.id.facebook_friends)
    protected RecyclerView facebookFriendsView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_invite_friends);
        ButterKnife.bind(this);

        // Setup action bar with back arrow
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Setup Facebook friends recycler view and adapter
        facebookFriendsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        friendAdapter = new FriendAdapter(new ArrayList<User>());
        facebookFriendsView.setAdapter(friendAdapter);

        // Retrieve fb friends
        FirebaseResourceManager.getFacebookFriends(new ResourceListener<List<User>>() {
            @Override
            public void onData(List<User> data) {
                if (data != null) {
                    // TODO remove all Facebook friends that are already in your Snaption friend list
                    // update the list of Facebook friends
                    friendAdapter.update(data);
                }
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
