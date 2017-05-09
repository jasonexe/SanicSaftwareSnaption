package com.snaptiongame.snaption.ui.games.add_friend_to_game;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.ui.friends.PersonViewHolder;

import java.util.List;

import static com.snaptiongame.snaption.Constants.GAME_DATA_PLAYER_PATH;

/**
 * Created by Jason Krein on 5/6/2017.
 */

public class AddFriendToGameAdapter extends RecyclerView.Adapter<ExistingGameFriendHolder> {
    private List<UserMetadata> friends;
    private Game gameData;

    public AddFriendToGameAdapter(List<UserMetadata> friends, Game gameData) {
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
                String userId = curUser.getId();
                String privacy = gameData.getIsPublic() ? Constants.PUBLIC : Constants.PRIVATE;
                FirebaseUploader.uploadObject(
                        String.format(GAME_DATA_PLAYER_PATH, privacy, gameData.getId(),
                                userId), 1);
                gameData.addPlayer(userId);
                int playerIdx = friends.indexOf(curUser);
                friends.remove(curUser);
                notifyItemRemoved(playerIdx);
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
