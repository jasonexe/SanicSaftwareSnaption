package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;

import java.util.List;

/**
 * @author Hristo Stoytchev
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private List<User> friends;

    public FriendsListAdapter(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       return FriendViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.friendName.setText(friend.getDisplayName());
        holder.friendEmail.setText(friend.getEmail());
        holder.friendEmail.setVisibility(TextUtils.isEmpty(friend.getEmail()) ? View.GONE : View.VISIBLE);
        FirebaseResourceManager.loadImageIntoView(friends.get(position).getImagePath(), holder.friendPhoto);
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
