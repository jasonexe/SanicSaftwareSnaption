package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.profile.ProfileGameViewHolder;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.util.List;

/**
 * @author Hristo Stoytchev
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListHolder> {
    private List<String> friends;
    private final FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();

    public FriendsListAdapter(List<String> friends) {
        this.friends = friends;
    }

    @Override
    public FriendsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list, parent, false);
        return new FriendsListHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsListHolder holder, int position) {
        // to avoid making another constant variable
        String friend = WallViewAdapter.USER_PATH + friends.get(position);

        // TODO change where the validFirebasePath method is called from
        // ensure the user id is a valid one to avoid errors
        if(WallViewAdapter.validFirebasePath(friend)) {
            // display the name and profile picture if a valid user is obtained from the user id
            firebaseResourceManager.retrieveSingleNoUpdates(friend, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    if (user != null) {
                        holder.friendName.setText(user.getDisplayName());
                        FirebaseResourceManager.loadImageIntoView(user.getImagePath(), holder.friendPicture);
                    }
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
