package com.snaptiongame.snaptionapp.ui.friends;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.HomeAppCompatActivity;

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
public class AddInviteFriendsActivity extends HomeAppCompatActivity {
    // TODO add friends from Google+
    // TODO add friends from phone contacts

    private FriendAdapter friendAdapter;
    private FriendsViewModel presenter;

    @BindView(R.id.login_provider_friends)
    protected RecyclerView loginProviderFriends;

    @BindView(R.id.login_provider_friends_label)
    protected TextView loginProviderFriendsLabel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initial view setup
        setContentView(R.layout.activity_add_invite_friends);
        ButterKnife.bind(this);

        // Login provider friends recycler view and adapter setup
        loginProviderFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        friendAdapter = new FriendAdapter(new ArrayList<Friend>());
        loginProviderFriends.setAdapter(friendAdapter);

        // Initialize the view model
        initializeViewModel();
    }

    private void initializeViewModel() {
        FirebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(), new ResourceListener<User>() {
            @Override
            public void onData(User user) {
                if (user != null) {
                    presenter = new FriendsViewModel(user);
                    setLoginProviderFriendsLabel();
                    populateLoginProviderFriends();
                }
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });
    }

    private void populateLoginProviderFriends() {
        presenter.getLoginProviderFriends(new ResourceListener<List<Friend>>() {
            @Override
            public void onData(List<Friend> friends) {
                if (friends != null) {
                    // update the list of login provider friends
                    friendAdapter.update(friends);
                }
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });
    }

    private void setLoginProviderFriendsLabel() {
        loginProviderFriendsLabel.setText(presenter.getLoginProviderLabel(getApplicationContext()));
    }
}
