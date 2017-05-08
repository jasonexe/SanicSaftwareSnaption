package com.snaptiongame.snaption.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import java.util.List;

/**
 * @author Hristo Stoytchev
 */
public class FriendsListAdapter extends RecyclerView.Adapter<PersonViewHolder> {
    private List<UserMetadata> friends;
    private AddInviteUserCallback addInviteUserCallback;
    private ProfileActivity.ProfileActivityCreator profileMaker;

    public interface AddInviteUserCallback {
        public void addInviteClicked(UserMetadata user);
    }

    public FriendsListAdapter(List<UserMetadata> friends, ProfileActivity.ProfileActivityCreator profileMaker) {
        this.friends = friends;
        this.profileMaker = profileMaker;
    }

    public FriendsListAdapter(List<UserMetadata> friends, AddInviteUserCallback addInviteUserCallback,
                              ProfileActivity.ProfileActivityCreator profileMaker) {
        this(friends, profileMaker);
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
        final UserMetadata friend = friends.get(position);
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
                profileMaker.create(friend.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addSingleItem(UserMetadata user) {
        friends.add(user);
        notifyDataSetChanged();
    }
}
