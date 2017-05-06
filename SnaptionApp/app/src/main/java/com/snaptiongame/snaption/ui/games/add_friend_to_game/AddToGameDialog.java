package com.snaptiongame.snaption.ui.games.add_friend_to_game;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.friends.FriendsListAdapter;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason Krein on 5/5/2017.
 */

public class AddToGameDialog extends AlertDialog {
    @BindView(R.id.friend_notice)
    TextView friendNotice;

    @BindView(R.id.friend_list)
    RecyclerView friendList;

    private Activity activity;
    private FirebaseUserResourceManager userFirebase = new FirebaseUserResourceManager();
    private AddFriendToGameAdapter addToGameAdapter;
    private GameMetadata gameData;
    /**
     * Constructor used when AddToGameDialog must be set after construction
     * @param activity current activity where dialog will be displayed
     */
    public AddToGameDialog(Activity activity, GameMetadata gameData) {
        super(activity);
        this.activity = activity;
        this.gameData = gameData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friends);
        ButterKnife.bind(this);
        LinearLayoutManager friendsViewManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(friendsViewManager);
        populateFriends();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        userFirebase.removeListener();
    }

    /**
     * Populates the friends list with your current snaption friends.
     */
    private void populateFriends() {
        final String userId = FirebaseUserResourceManager.getUserId();
        if (FirebaseUserResourceManager.getUserId() != null) {
            userFirebase.getUserFriendsWithUpdates(userId, new ResourceListener<Map<String, Integer>>() {
                @Override
                public void onData(Map<String, Integer> userIds) {
                    if (userIds != null) {
                        System.out.println("Got users");
                        friendNotice.setVisibility(View.GONE);
                        List<UserMetadata> users = new ArrayList<>();
                        addToGameAdapter = new AddFriendToGameAdapter(users, gameData);
                        friendList.setAdapter(addToGameAdapter);
                        loadUsers(userIds);
                    }
                    else {
                        friendNotice.setVisibility(View.VISIBLE);
                        friendNotice.setText(R.string.empty_friends);
                    }
                }

                @Override
                public Class getDataType() {
                    return Map.class;
                }
            });
        }
    }

    private void loadUsers(Map<String, Integer> uids) {
        FirebaseUserResourceManager.getUsersMetadataByIds(uids, new ResourceListener<UserMetadata>() {
            @Override
            public void onData(UserMetadata user) {
                if (user != null) {
                    addToGameAdapter.addSingleItem(user);
                }
            }

            @Override
            public Class getDataType() {
                return UserMetadata.class;
            }
        });
    }
}
