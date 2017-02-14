package com.snaptiongame.snaptionapp.ui.friends;

import android.content.Context;
import android.text.TextUtils;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import java.util.List;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.FRIENDS_PATH;
import static com.snaptiongame.snaptionapp.servercalls.Uploader.ITEM_ALREADY_EXISTS_ERROR;
import static com.snaptiongame.snaptionapp.servercalls.Uploader.UploadListener;

/**
 * FriendsViewModel is used by a view to retrieve and display information about the current user's
 * actual and potential friends
 */
public class FriendsViewModel {
    private User user;
    private Uploader uploader;

    public FriendsViewModel(User user, Uploader uploader) {
        this.user = user;
        this.uploader = uploader;
    }

    public void getLoginProviderFriends(final ResourceListener<Friend> listener) {
        // if the user logged in with Facebook
        if (!TextUtils.isEmpty(user.getFacebookId())) {
            // retrieve user's friends to use for filtering out Facebook friends that are already
            // their friends
            FirebaseResourceManager.retrieveStringListNoUpdates(String.format(FRIENDS_PATH, user.getId()),
                    new ResourceListener<List<String>>() {
                @Override
                public void onData(List<String> data) {
                    FirebaseResourceManager.getFacebookFriends(user, data, listener);
                }

                @Override
                public Class getDataType() {
                    return String.class;
                }
            });

        }
        // else the user logged in with Google+
        else {
            // TODO get Google+ friends
        }
    }

    public String getLoginProviderLabel(Context appContext) {
        if (!TextUtils.isEmpty(user.getFacebookId())) {
            return appContext.getString(R.string.fb_friends);
        }
        else {
            return appContext.getString(R.string.google_friends);
        }
    }

    public void addFriend(Friend friend, UploadListener listener) {
        uploader.addFriend(user, friend, listener);
    }

    public String getAddedFriendText(Context appContext, String friendName, boolean successfulAdd,
                                     String errorMessage) {
        return successfulAdd ?
                String.format(appContext.getString(R.string.added_friend), friendName) :
                TextUtils.isEmpty(errorMessage) || !errorMessage.equals(ITEM_ALREADY_EXISTS_ERROR) ?
                        String.format(appContext.getString(R.string.problem_adding_friend), friendName) :
                        String.format(appContext.getString(R.string.already_friend), friendName) ;
    }
}
