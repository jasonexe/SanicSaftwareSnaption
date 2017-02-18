package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;

import java.util.List;

/**
 * AddFriendAdapter is a RecyclerView.Adapter used for FriendViewHolder
 *
 * @author Brittany Berlanga
 */
public class AddFriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private List<Friend> friends;
    private AddInviteFriendCallback callback;

    public interface AddInviteFriendCallback {
        public void addInviteClicked(Friend friend);
    }

    public AddFriendAdapter(List<Friend> friends, AddInviteFriendCallback callback) {
        this.friends = friends;
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FriendViewHolder viewHolder =  FriendViewHolder.newInstance(parent);
        if (callback != null) {
            viewHolder.addInviteButton.setVisibility(View.VISIBLE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        final Friend friend = friends.get(position);
        holder.friendName.setText(friend.displayName);
        holder.friendEmail.setText(friend.email);
        holder.friendEmail.setVisibility(TextUtils.isEmpty(friend.email) ? View.GONE : View.VISIBLE);
        FirebaseResourceManager.loadSmallFbPhotoIntoImageView(friend.facebookId, holder.friendPhoto);
        if (callback != null) {
            holder.addInviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.addInviteClicked(friend);
                }
            });
        }
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
