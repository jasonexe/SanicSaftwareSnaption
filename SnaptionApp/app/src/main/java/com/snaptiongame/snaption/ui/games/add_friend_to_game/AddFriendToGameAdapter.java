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

import java.util.Collections;
import java.util.List;

import static com.snaptiongame.snaption.Constants.GAME_DATA_PLAYER_PATH;

/**
 * Created by Jason Krein on 5/6/2017.
 */

public class AddFriendToGameAdapter extends RecyclerView.Adapter<ExistingGameFriendHolder> {
    private List<UserMetadata> friends;
    private Game gameData;
    private View addedText;

    public AddFriendToGameAdapter(List<UserMetadata> friends, Game gameData, View addedText) {
        this.friends = friends;
        this.gameData = gameData;
        this.addedText = addedText;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public void onBindViewHolder(final ExistingGameFriendHolder holder, int position) {
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
                if(friends.size() == 0) {
                    addedText.setVisibility(View.VISIBLE);
                } else {
                    addedText.setVisibility(View.GONE);
                }
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
        Collections.sort(friends);
        int insertedPos = friends.indexOf(metadata);
        notifyItemInserted(insertedPos);
    }
}
