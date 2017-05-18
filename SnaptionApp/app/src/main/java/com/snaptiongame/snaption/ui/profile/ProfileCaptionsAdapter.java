package com.snaptiongame.snaption.ui.profile;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.ui.games.GameActivity;

import java.util.List;

import static com.snaptiongame.snaption.ui.games.GameActivity.USE_GAME_ACCESS;
import static com.snaptiongame.snaption.ui.games.GameActivity.USE_GAME_ID;

/**
 * The adapter for the recycler view that holds the user's captions
 * Created by Jason Krein on 2/12/2017.
 */

public class ProfileCaptionsAdapter extends RecyclerView.Adapter<ProfileCaptionsViewHolder> {
    private List<Caption> captions;

    public ProfileCaptionsAdapter(List<Caption> captions) {
        this.captions = captions;
    }

    @Override
    public void onBindViewHolder(ProfileCaptionsViewHolder holder, int position) {
        Caption curCaption = captions.get(position);
        final String gameId = curCaption.getGameId();
        final String access = curCaption.retrieveIsPublic() ? Constants.PUBLIC : Constants.PRIVATE;

        holder.captionText.setText(curCaption.retrieveCaptionText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {;
                Intent gamePageIntent = new Intent(view.getContext(), GameActivity.class);
                gamePageIntent.putExtra(USE_GAME_ID, gameId);
                gamePageIntent.putExtra(USE_GAME_ACCESS, access);
                view.getContext().startActivity(gamePageIntent);
            }
        });
        holder.captionUpvotes.setText(String.valueOf(curCaption.retrieveNumUpvotes()));
        // Make sure it's visible. Gone by default so that the cards in the game don't need to change.
        holder.captionUpvotes.setVisibility(View.VISIBLE);
    }

    @Override
    public ProfileCaptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_captions, parent, false);
        return new ProfileCaptionsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return captions.size();
    }

    public void addCaption(Caption caption) {
        captions.add(caption);
        notifyItemInserted(captions.size() - 1);
    }
}
