package com.snaptiongame.snaption.ui.games.players;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brittanyberlanga on 5/9/17.
 */

public class GamePlayerView extends LinearLayout {
    private static final int MAX_PLAYERS_SHOWN = 3;

    public GamePlayerView(Context context) {
        super(context);
    }

    public GamePlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GamePlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPlayers(List<String> playerIds) {
        boolean showOverflow = playerIds.size() > MAX_PLAYERS_SHOWN;
        int overflowCount = showOverflow ? playerIds.size() - MAX_PLAYERS_SHOWN : 0;
        removeAllViews();
        // if the overflow view should be shown, get the players to be shown
        if (showOverflow) {
            playerIds = playerIds.subList(0, MAX_PLAYERS_SHOWN);
        }
        // add a player view for every player shown
        for (String playerId : playerIds) {
            addPlayerView(String.format(Constants.USER_PROFILE_PHOTO_PATH, playerId));
        }
        // if the overflow view should be shown, add the overflow view
        if (showOverflow) {
            addOverflowView(overflowCount);
        }
    }

    private void addPlayerView(String playerPath) {
        View playerView = LayoutInflater.from(getContext()).inflate(R.layout.view_player_item,
                this, false);
        addView(playerView);
        GamePlayerViewHolder viewHolder = new GamePlayerViewHolder(playerView);
        FirebaseResourceManager.loadImageIntoView(playerPath, viewHolder.playerImage);
    }

    private void addOverflowView(int overflowCount) {
        View overflowView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_player_overflow_item, this, false);
        addView(overflowView);
        GamePlayerOverflowViewHolder viewHolder = new GamePlayerOverflowViewHolder(overflowView);
        viewHolder.overflowText.setText(String
                .format(getContext().getString(R.string.overflow_text), overflowCount));
    }

    protected class GamePlayerViewHolder {
        @BindView(R.id.player_image)
        protected ImageView playerImage;

        GamePlayerViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    protected class GamePlayerOverflowViewHolder {
        @BindView(R.id.overflow_text)
        protected TextView overflowText;

        GamePlayerOverflowViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
