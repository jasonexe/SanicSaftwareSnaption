package com.snaptiongame.snaptionapp.ui.profile;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.ui.friends.AddInviteFriendsActivity;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;

import java.util.List;

import static com.snaptiongame.snaptionapp.R.string.profile;
import static com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter.PHOTO_PATH;

/**
 * Created by jason_000 on 2/12/2017.
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
            public void onClick(View view) {
                String gameId = curCaption.getGameId();
                Intent intent = new Intent(view.getContext(), GameActivity.class);
                //TODO change this to whatever Cameron's code needs.
                intent.putExtra(PHOTO_PATH, gameId);
                view.getContext().startActivity(intent);
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
