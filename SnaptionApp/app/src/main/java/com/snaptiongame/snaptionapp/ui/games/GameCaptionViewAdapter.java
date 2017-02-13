package com.snaptiongame.snaptionapp.ui.games;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Provides a binding for captions to be displayed using a RecyclerView in GameActivity.
 *
 * @author Cameron Geehr
 */

public class GameCaptionViewAdapter extends RecyclerView.Adapter<CaptionViewHolder> {

    private List<Caption> items;
    FirebaseResourceManager firebaseResourceManager;

    private class UpvoteClickListener implements View.OnClickListener {
        Caption caption;

        public UpvoteClickListener(Caption caption) {
            this.caption = caption;
        }

        @Override
        public void onClick(View upvote) {
            handleClickUpvote((ImageView) upvote, caption);
        }
    }

    public GameCaptionViewAdapter(List<Caption> items) {
        this.items = new ArrayList<>(items);
        firebaseResourceManager = new FirebaseResourceManager();
    }

    /**
     * Sets the text, images, and click handlers for the Caption view
     *
     * @param holder The object that contains the views to display
     * @param position The position in the list of Captions
     */
    @Override
    public void onBindViewHolder(final CaptionViewHolder holder, int position) {
        Caption caption = items.get(position);

        String userPath = FirebaseResourceManager.getUserPath(caption.getUserId());
        // Get information about the captioner to display it
        firebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
            @Override
            public void onData(User user) {
                if (user != null) {
                    holder.captionerName.setText(user.getDisplayName());
                    FirebaseResourceManager.loadImageIntoView(user.getImagePath(), holder.captionerPhoto);
                }
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });

        holder.captionText.setText(caption.retrieveCaptionText());

        holder.numberUpvotes.setText(Integer.valueOf(caption.getVotes()).toString());
        //holder.upvote.setOnClickListener(new UpvoteClickListener(caption));
        //TODO change the default drawable for upvote based on whether the user has upvoted the caption
    }

    @Override
    public CaptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_caption_item, parent, false);
        return new CaptionViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    private void handleClickUpvote(ImageView upvote, Caption caption) {
        //Using the deprecated method because the current version isn't compatible with our min API
        //TODO check if the user has upvoted the caption already
        //upvote.setImageDrawable(upvote.getResources().getDrawable(R.drawable.thumbs_up_filled));
        //upvote.setImageDrawable(upvote.getResources().getDrawable(R.drawable.thumbs_up_blank));
    }

    /**
     * Adds more captions into the list of captions.
     *
     * @param newCaption A caption to add to the list
     */
    public void addCaption(Caption newCaption) {
        if(!items.contains(newCaption)) {
            items.add(newCaption);
        }
        this.notifyDataSetChanged();
    }

}
