package com.snaptiongame.snaptionapp.ui.profile;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseGameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.GameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.util.List;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseGameResourceManager.GAME_TABLE;
import static com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter.PHOTO_PATH;

/**
 * The adapter for the recycler view that holds the user's captions
 * Created by Jason Krein on 2/12/2017.
 */

public class ProfileCaptionsAdapter extends RecyclerView.Adapter<ProfileCaptionsViewHolder> {
    List<Caption> captions;

    public ProfileCaptionsAdapter(List<Caption> captions) {
        this.captions = captions;
    }

    @Override
    public void onBindViewHolder(ProfileCaptionsViewHolder holder, int position) {
        final Caption curCaption = captions.get(position);
        holder.captionText.setText(curCaption.retrieveCaptionText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String gameId = curCaption.getGameId();
                FirebaseResourceManager.retrieveSingleNoUpdates(
                        FirebaseGameResourceManager.GAME_TABLE + "/" + gameId,
                        new ResourceListener<Game>() {
                            @Override
                            public void onData(Game data) {
                                Intent gamePageIntent = new Intent(view.getContext(), GameActivity.class);
                                gamePageIntent.putExtra(WallViewAdapter.GAME, data);
                                view.getContext().startActivity(gamePageIntent);
                            }

                            @Override
                            public Class getDataType() {
                                return Game.class;
                            }
                        });

            }
        });
    }

    @Override
    public ProfileCaptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_option_layout, parent, false);
        return new ProfileCaptionsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return captions.size();
    }
}
