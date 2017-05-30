package com.snaptiongame.snaption.ui.friends;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;

import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaption.servercalls.Uploader.ITEM_ALREADY_EXISTS_ERROR;
import static com.snaptiongame.snaption.servercalls.Uploader.UploadListener;

/**
 * FriendsViewModel is used by a view to retrieve and display information about the current user's
 * actual and potential friends
 */
public class FriendsViewModel {
    private UserMetadata user;
    private Uploader uploader;

    public FriendsViewModel(UserMetadata user, Uploader uploader) {
        this.user = user;
        this.uploader = uploader;
    }

    public void getLoginProviderFriends(final ResourceListener<Friend> listener) {
        List<String> providers = FirebaseUserResourceManager.getProviders();
        // if the user logged in with Facebook
        if (providers.contains(FacebookAuthProvider.PROVIDER_ID)) {
            // retrieve user's friends to use for filtering out Facebook friends that are already
            // their friends
            FirebaseResourceManager.retrieveStringMapNoUpdates(String.format(Constants.USER_FRIENDS_PATH, user.getId()),
                    new ResourceListener<Map<String, Integer>>() {
                @Override
                public void onData(Map<String, Integer> data) {
                    FirebaseUserResourceManager.getFacebookFriends(user, data, listener);
                }

                @Override
                public Class getDataType() {
                    return String.class;
                }
            });

        }
        // else the user logged in with Google+
        else if (providers.contains(GoogleAuthProvider.PROVIDER_ID)) {
            // TODO get Google+ friends
        }
    }

    public boolean showLoginProviderLabel() {
        List<String> providers = FirebaseUserResourceManager.getProviders();
        // true if the user logged in with Facebook
        return providers.contains(FacebookAuthProvider.PROVIDER_ID);
    }

    public void addFriend(Friend friend, UploadListener listener) {
        uploader.addFriend(user, friend, listener);
    }

    /**
     * Returns text to be displayed to the user after a friend has been added / attempted to be
     * added and failed.
     *
     * @param appContext Application context
     * @param friendName display name of added friend
     * @param successfulAdd whether the add was successful
     * @param errorMessage Uploader error message. Null if the add was successful
     * @return
     */
    public String getAddedFriendText(Context appContext, String friendName, boolean successfulAdd,
                                     String errorMessage) {
        // if adding a friend was successful, return text explaining the friend was added
        // if adding a friend failed, return text explaining the add failed
        //      if the error message was ITEM_ALREADY_EXISTS_ERROR, return text explaining the
        //          friend is already a friend of the user
        //      else return a generic text explaining the friend could not be added
        return successfulAdd ?
                String.format(appContext.getString(R.string.added_friend), friendName) :
                TextUtils.isEmpty(errorMessage) || !errorMessage.equals(ITEM_ALREADY_EXISTS_ERROR) ?
                        String.format(appContext.getString(R.string.problem_adding_friend), friendName) :
                        String.format(appContext.getString(R.string.already_friend), friendName) ;
    }

    public int getFacebookButtonVisibility() {
        List<String> providers = FirebaseUserResourceManager.getProviders();
        // if the user logged in with Facebook
        if (providers.contains(FacebookAuthProvider.PROVIDER_ID)) {
            return View.VISIBLE;
        }
        else {
            return View.GONE;
        }
    }
}
