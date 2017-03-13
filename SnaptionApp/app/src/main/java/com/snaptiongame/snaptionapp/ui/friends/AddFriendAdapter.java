package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.ui.profile.ProfileActivity;

import java.util.List;

/**
 * AddFriendAdapter is a RecyclerView.Adapter used for PersonViewHolder
 *
 * @author Brittany Berlanga
 */
public class AddFriendAdapter extends RecyclerView.Adapter<PersonViewHolder> {
    private List<Friend> friends;
    private AddInviteFriendCallback callback;
    private ProfileActivity.ProfileActivityCreator profileMaker;

    public interface AddInviteFriendCallback {
        public void addInviteClicked(Friend friend);
    }

    public AddFriendAdapter(List<Friend> friends, AddInviteFriendCallback callback,
                            ProfileActivity.ProfileActivityCreator profileMaker) {
        this.friends = friends;
        this.callback = callback;
        this.profileMaker = profileMaker;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PersonViewHolder viewHolder = PersonViewHolder.newInstance(parent);
        if (callback != null) {
            viewHolder.addInviteButton.setVisibility(View.VISIBLE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        final Friend friend = friends.get(position);
        holder.name.setText(friend.displayName);
        holder.email.setText(friend.email);
        holder.email.setVisibility(TextUtils.isEmpty(friend.email) ? View.GONE : View.VISIBLE);
        FirebaseResourceManager.loadSmallFbPhotoIntoImageView(friend.facebookId, holder.photo);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileMaker.create(friend.getId());
            }
        });
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
