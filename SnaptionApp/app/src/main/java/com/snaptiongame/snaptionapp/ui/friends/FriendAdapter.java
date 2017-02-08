package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;

import java.util.List;

/**
 * FriendAdapter is a RecyclerView.Adapter used for FriendViewHolder
 *
 * @author Brittany Berlanga
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private List<User> friends;
    public FriendAdapter(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.friendName.setText(friend.getDisplayName());
        FirebaseResourceManager.loadSmallFbPhotoIntoImageView(friend.getFacebookId(), holder.friendPhoto);
    }

    public void update(List<User> friends) {
        this.friends.addAll(friends);
        notifyDataSetChanged();
    }
}
