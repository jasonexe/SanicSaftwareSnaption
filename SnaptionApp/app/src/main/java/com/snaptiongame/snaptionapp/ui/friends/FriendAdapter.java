package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;

import java.util.List;

/**
 * FriendAdapter is a RecyclerView.Adapter used for FriendViewHolder
 *
 * @author Brittany Berlanga
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private List<Friend> friends;
    private AddInviteFriendCallback callback;

    public interface AddInviteFriendCallback {
        public void addInviteClicked(Friend friend);
    }

    public FriendAdapter(List<Friend> friends, AddInviteFriendCallback callback) {
        this.friends = friends;
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        final Friend friend = friends.get(position);
        holder.friendName.setText(friend.displayName);
        FirebaseResourceManager.loadSmallFbPhotoIntoImageView(friend.facebookId, holder.friendPhoto);
        holder.addInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addInviteClicked(friend);
            }
        });
    }

    /**
     * Adds a single Friend to the list of friends
     * @param friend
     */
    public void addSingleItem(Friend friend) {
        this.friends.add(friend);
        notifyItemInserted(this.friends.size() - 1);
    }

    /**
     * Removes a single Friend from the list of friends
     * @param friend
     */
    public void removeSingleItem(Friend friend) {
        int ndx = this.friends.indexOf(friend);
        if (ndx >= 0) {
            this.friends.remove(ndx);
            notifyItemRemoved(ndx);
        }
    }
}
