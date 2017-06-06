package com.snaptiongame.snaption.ui.games.add_friend_to_game;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseDeepLinkCreator;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason Krein on 5/5/2017.
 */

public class AddToGameDialog extends AlertDialog {

    @BindView(R.id.friend_list)
    RecyclerView friendList;

    @BindView(R.id.deep_link_button)
    Button deepLinkButton;

    @BindView(R.id.intent_load_progress)
    public View progressSpinner;

    @BindView(R.id.all_friends_added_text)
    public TextView allAddedText;

    private FragmentActivity activity;
    private FirebaseUserResourceManager userFirebase = new FirebaseUserResourceManager();
    private AddFriendToGameAdapter addToGameAdapter;
    private Game gameData;
    private Bitmap photoPreview;
    private String sampleCaption;
    /**
     * Constructor used when AddToGameDialog must be set after construction
     * @param activity current activity where dialog will be displayed
     */
    public AddToGameDialog(FragmentActivity activity, Game gameData, Bitmap bmp, String sampleCaption) {
        super(activity);
        this.activity = activity;
        this.gameData = gameData;
        this.photoPreview = bmp;
        this.sampleCaption = sampleCaption;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_invite);
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

    @OnClick(R.id.deep_link_button)
    public void createDeepLink() {
        FirebaseDeepLinkCreator.createGameInviteIntent(activity, gameData, progressSpinner, photoPreview, sampleCaption);
    }

    /**
     * Populates the friends list with your current snaption friends.
     */
    private void populateFriends() {
        final String userId = FirebaseUserResourceManager.getUserId();
        if (userId != null) {
            userFirebase.getUserFriendsWithUpdates(userId, new ResourceListener<Map<String, Integer>>() {
                @Override
                public void onData(Map<String, Integer> userIds) {
                    if (userIds != null) {
                        if(gameData.getPlayers() != null) {
                            for(String userId : gameData.getPlayers().keySet()) {
                                userIds.remove(userId);
                            }
                            // remove picker from possible friends to add
                            userIds.remove(gameData.getPickerId());
                        }
                        List<UserMetadata> users = new ArrayList<>();
                        addToGameAdapter = new AddFriendToGameAdapter(users, gameData, allAddedText);
                        friendList.setAdapter(addToGameAdapter);
                        if(userIds.size() == 0) {
                            allAddedText.setVisibility(View.VISIBLE);
                        } else {
                            allAddedText.setVisibility(View.GONE);
                        }
                        loadUsers(userIds);
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
