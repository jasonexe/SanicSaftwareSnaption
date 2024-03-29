package com.snaptiongame.snaption.ui.profile;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.ui.games.GameActivity;

import java.util.List;


/**
 * Created by austinrobarts on 1/29/17.
 */

public class ProfileGamesAdapter extends RecyclerView.Adapter<ProfileGameViewHolder> {
    private List<GameMetadata> games;

    public ProfileGamesAdapter(List<GameMetadata> games) {
        this.games = games;
    }

    @Override
    public ProfileGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_game_item, parent, false);
        return new ProfileGameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProfileGameViewHolder holder, int position) {
        //get last item and show it first
        final GameMetadata game = games.get(games.size() - 1 - position);
        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // allows the image to be clipped with rounded edges
            holder.photo.setClipToOutline(true);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gamePageIntent = new Intent(view.getContext(), GameActivity.class);
                gamePageIntent.putExtra(Constants.GAME, game);
                view.getContext().startActivity(gamePageIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void addGame(GameMetadata game) {
        games.add(game);
        this.notifyDataSetChanged();
    }
}
