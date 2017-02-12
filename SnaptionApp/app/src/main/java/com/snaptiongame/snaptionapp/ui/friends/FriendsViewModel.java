package com.snaptiongame.snaptionapp.ui.friends;

import android.content.Context;
import android.text.TextUtils;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.List;

/**
 * FriendsViewModel is used by a view to retrieve and display information about the current user's
 * actual and potential friends
 */
public class FriendsViewModel {
    private User user;

    public FriendsViewModel(User user) {
        this.user = user;
    }

    public void getLoginProviderFriends(final ResourceListener<List<Friend>> listener) {
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
}
