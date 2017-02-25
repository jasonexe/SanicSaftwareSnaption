package com.snaptiongame.snaptionapp.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.appinvite.AppInvite;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.ui.HomeAppCompatActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    //created app link from Facebook to link to our application when its on Google Play
    private static final String appLinkUrl = "https://fb.me/1863284123957626";
    //currently goes to the Snaption icon on google search
    private static final String previewImageUrl = "http://static1.squarespace.com/static/55a5836fe4b0b0843a0e2862/t/571fefa0f8baf30a23c535dd/1473092005381/";

    // Start of any deep link that you create
    private String deepLink = "https://snaptiongame.com";
    // Pre-generated deep link to the home screen, allows for tracking through firebase console
    private String homescreenDeepLink = "https://ba63n.app.goo.gl/a10w";

    private Uploader uploader;
    private AddFriendAdapter addFriendAdapter;
    private FriendsViewModel viewModel;

    @BindView(R.id.login_provider_friends)
    protected RecyclerView loginProviderFriends;

    @BindView(R.id.login_provider_friends_label)
    protected TextView loginProviderFriendsLabel;

    @BindView(R.id.invite_friends_button)
    protected Button inviteFriendsButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initial view setup
        setContentView(R.layout.activity_add_invite_friends);
        ButterKnife.bind(this);

        // Login provider friends recycler view and adapter setup
        setupLoginProviderView();

        // Initialize the uploader and view model
        uploader = new FirebaseUploader();
        initializeViewModel();
    }

    @OnClick(R.id.invite_friends_button)
    public void inviteFriends() {
        //display facebook invite dialog
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }

    @OnClick(R.id.email_invite)
    public void createEmailIntent() {
        // TODO if this was started from the create game screen, get a custom deep link
        // from FirebaseDeepLinkCreator class. URL will be https://snaptiongame.com/games/<GAME_ID>
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.join_snaption_subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, String.format(getApplicationContext()
                .getString(R.string.join_snaption_email_body), homescreenDeepLink));
        startActivity(Intent.createChooser(emailIntent, "Send Email"));

    }

    private void setupLoginProviderView() {
        loginProviderFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        addFriendAdapter = new AddFriendAdapter(new ArrayList<Friend>(), new AddFriendAdapter.AddInviteFriendCallback() {
            @Override
            public void addInviteClicked(final Friend friend) {
                viewModel.addFriend(friend, new Uploader.UploadListener() {
                    @Override
                    public void onComplete() {
                        // notify user
                        Toast.makeText(AddInviteFriendsActivity.this,
                                viewModel.getAddedFriendText(AddInviteFriendsActivity.this,
                                        friend.displayName, true, null), Toast.LENGTH_LONG).show();
                        // remove friend from view
                        addFriendAdapter.removeSingleItem(friend);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // notify user
                        Toast.makeText(AddInviteFriendsActivity.this,
                                viewModel.getAddedFriendText(AddInviteFriendsActivity.this,
                                        friend.displayName, false, errorMessage), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        loginProviderFriends.setAdapter(addFriendAdapter);
    }

    @SuppressWarnings("ResourceType")
    private void initializeViewModel() {
        FirebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(),
                new ResourceListener<User>() {
            @Override
            public void onData(User user) {
                if (user != null) {
                    viewModel = new FriendsViewModel(user, uploader);
                    setLoginProviderFriendsLabel();
                    populateLoginProviderFriends();
                    //must suppress resource type for this method to work
                    inviteFriendsButton.setVisibility(viewModel.getFacebookButtonVisibility());
                    //TODO: Set visibility of Google+ invite button here after facebook invite
                }
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });
    }

    private void populateLoginProviderFriends() {
        viewModel.getLoginProviderFriends(new ResourceListener<Friend>() {
            @Override
            public void onData(Friend friend) {
                if (friend != null) {
                    // update the list of login provider friends
                    addFriendAdapter.addSingleItem(friend);
                }
            }

            @Override
            public Class getDataType() {
                return Friend.class;
            }
        });
    }

    private void setLoginProviderFriendsLabel() {
        loginProviderFriendsLabel.setText(viewModel.getLoginProviderLabel(getApplicationContext()));
    }
}
