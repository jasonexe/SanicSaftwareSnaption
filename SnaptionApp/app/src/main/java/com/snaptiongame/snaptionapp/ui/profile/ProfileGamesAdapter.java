package com.snaptiongame.snaptionapp.ui.profile;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;

import java.util.List;

import static com.snaptiongame.snaptionapp.Constants.CLIP_TO_OUTLINE_MIN_SDK;

/**
 * Created by austinrobarts on 1/29/17.
 */

public class ProfileGamesAdapter extends RecyclerView.Adapter<ProfileGameViewHolder> {
    private List<Game> games;

    public ProfileGamesAdapter(List<Game> games) {
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
        final Game game = games.get(games.size() - 1 - position);
        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);

        if (Build.VERSION.SDK_INT >= CLIP_TO_OUTLINE_MIN_SDK) {
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

    public void addGame(Game game) {
        games.add(game);
        this.notifyDataSetChanged();
    }
}
