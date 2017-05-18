package com.snaptiongame.snaption.ui.user;

import android.content.Context;

import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;

import java.util.List;

/**
 * Created by brittanyberlanga on 5/10/17.
 */

public abstract class UserListViewModel {
    protected List<String> userIds;
    private UserListAdapter userListAdapter;

    public UserListViewModel(List<String> userIds) {
        this.userIds = userIds;
    }

    public UserListAdapter getAdapter(Context context) {
        if (userListAdapter == null) {
            initializeAdapter(context);
        }
        return userListAdapter;
    }

    private void initializeAdapter(Context context) {
        userListAdapter = createUserListAdapter(context);
        for (String userId : userIds) {
            FirebaseUserResourceManager.getUserMetadataById(userId,
                    new ResourceListener<UserMetadata>() {
                        @Override
                        public void onData(UserMetadata data) {
                            if (data != null) {
                                userListAdapter.setUser(data);
                            }
                        }

                        @Override
                        public Class getDataType() {
                            return UserMetadata.class;
                        }
                    });
        }
    }

    public abstract UserListAdapter createUserListAdapter(Context context);
}
