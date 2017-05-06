package com.snaptiongame.snaption.ui.games.add_friend_to_game;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.ui.friends.PersonViewHolder;

import java.util.List;

/**
 * Created by Jason Krein on 5/6/2017.
 */

public class AddFriendToGameAdapter extends RecyclerView.Adapter<ExistingGameFriendHolder> {
    private List<UserMetadata> friends;
    private GameMetadata gameData;

    public AddFriendToGameAdapter(List<UserMetadata> friends, GameMetadata gameData) {
        this.friends = friends;
        this.gameData = gameData;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public void onBindViewHolder(ExistingGameFriendHolder holder, int position) {
        final UserMetadata curUser = friends.get(position);
        holder.email.setText(curUser.getEmail());
        holder.name.setText(curUser.getDisplayName());
        FirebaseResourceManager.loadImageIntoView(curUser.getImagePath(), holder.photo);
        holder.addInviteButton.setVisibility(View.VISIBLE);
        holder.addInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Would add user w/ name: " + curUser.getDisplayName() + " To game id: " + gameData.getId());
            }
        });
    }

    @Override
    public ExistingGameFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ExistingGameFriendHolder viewHolder = ExistingGameFriendHolder.newInstance(parent);
        return viewHolder;
    }

    public void addSingleItem(UserMetadata metadata) {
        friends.add(metadata);
        notifyItemInserted(this.friends.size() - 1);
    }
}
