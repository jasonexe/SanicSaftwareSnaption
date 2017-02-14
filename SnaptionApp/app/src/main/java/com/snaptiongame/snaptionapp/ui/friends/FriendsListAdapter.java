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
    private List<User> friends;

    public FriendsListAdapter(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public FriendsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list, parent, false);
        return new FriendsListHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsListHolder holder, int position) {
        holder.friendName.setText(friends.get(position).getDisplayName());
        FirebaseResourceManager.loadImageIntoView(friends.get(position).getImagePath(), holder.friendPicture);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addSingleItem(User user) {
        friends.add(user);
        notifyItemInserted(friends.size() - 1);
    }
}
