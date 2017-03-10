package com.snaptiongame.snaptionapp.ui.friends;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.ui.profile.ProfileActivity;
import com.snaptiongame.snaptionapp.ui.profile.ProfileFragment;

import java.util.List;

/**
 * @author Hristo Stoytchev
 */
public class FriendsListAdapter extends RecyclerView.Adapter<PersonViewHolder> {
    private List<User> friends;
    private AddInviteUserCallback addInviteUserCallback;
    private UserClickCallback userClickCallback;

    public interface AddInviteUserCallback {
        public void addInviteClicked(User user);
    }

    public interface UserClickCallback {
        public void clickedOnUser(String userId);
    }

    public FriendsListAdapter(List<User> friends, UserClickCallback userClickCallback) {
        this.friends = friends;
        this.userClickCallback = userClickCallback;
    }

    public FriendsListAdapter(List<User> friends, AddInviteUserCallback addInviteUserCallback,
                              UserClickCallback userClickCallback) {
        this(friends, userClickCallback);
        this.addInviteUserCallback = addInviteUserCallback;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       PersonViewHolder holder = PersonViewHolder.newInstance(parent);
       if (addInviteUserCallback != null) {
           holder.addInviteButton.setVisibility(View.VISIBLE);
       }
       return holder;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, int position) {
        final User friend = friends.get(position);
        holder.name.setText(friend.getDisplayName());
        holder.email.setText(friend.getEmail());
        holder.email.setVisibility(TextUtils.isEmpty(friend.getEmail()) ? View.GONE : View.VISIBLE);
        FirebaseResourceManager.loadImageIntoView(friends.get(position).getImagePath(), holder.photo);
        if (addInviteUserCallback != null) {
            holder.addInviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addInviteUserCallback.addInviteClicked(friend);
                }
            });
        }
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userClickCallback.clickedOnUser(friend.getId());
            }
        });
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
