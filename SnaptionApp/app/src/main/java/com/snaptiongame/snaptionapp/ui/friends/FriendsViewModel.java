package com.snaptiongame.snaptionapp.ui.friends;

import android.content.Context;
import android.text.TextUtils;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

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
        // TODO remove all Facebook friends that are already in your Snaption friend list
        // if the user logged in with Facebook
        if (!TextUtils.isEmpty(user.getFacebookId())) {
            FirebaseResourceManager.getFacebookFriends(user.getFacebookId(), listener);
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

    public void addFriend(Friend friend, Uploader.UploadListener listener) {
        uploader.addFriend(user, friend, listener);
    }

    public String getAddedFriendText(Context appContext, String friendName, boolean successfulAdd) {
        return successfulAdd ?
                (String.format(appContext.getString(R.string.added_friend), friendName)) :
                (String.format(appContext.getString(R.string.problem_adding_friend), friendName));
    }
}
