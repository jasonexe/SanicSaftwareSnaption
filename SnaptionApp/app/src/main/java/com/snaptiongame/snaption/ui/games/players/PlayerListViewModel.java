package com.snaptiongame.snaption.ui.games.players;

import android.content.Context;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;
import com.snaptiongame.snaption.ui.user.UserListAdapter;
import com.snaptiongame.snaption.ui.user.UserListViewModel;

import java.util.List;

/**
 * Created by brittanyberlanga on 5/10/17.
 */

public class PlayerListViewModel extends UserListViewModel {
    private String pickerId;
    private String actionText;

    public PlayerListViewModel(List<String> userIds, String pickerId) {
        super(userIds);
        this.pickerId = pickerId;
    }

    @Override
    public UserListAdapter createUserListAdapter(Context context) {
        actionText = context.getString(R.string.leave_game);
        return new PlayerListAdapter(userIds, ProfileActivity.getProfileActivityCreator(context));
    }

    private class PlayerListAdapter extends UserListAdapter {
        PlayerListAdapter(List<String> userIds,
                                 ProfileActivity.ProfileActivityCreator profileMaker) {
            super(userIds, profileMaker);
        }

        @Override
        public void onClickAction(UserMetadata user) {
            // remove the user
        }

        @Override
        public String actionText(UserMetadata user) {
            return FirebaseUserResourceManager.getUserId().equals(user.getId()) &&
                    !pickerId.equals(user.getId()) ? actionText : null;
        }
    }
}
