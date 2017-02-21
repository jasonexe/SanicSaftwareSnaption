package com.snaptiongame.snaptionapp.ui.games;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.OnClick;

/**
 * Provides a binding for captions to be displayed using a RecyclerView in GameActivity.
 *
 * @author Cameron Geehr
 */

public class GameCaptionViewAdapter extends RecyclerView.Adapter<CaptionViewHolder> {

    private List<Caption> items;
    private static final String UPVOTES_PATH = "games/%s/captions/%s/votes";
    FirebaseResourceManager firebaseResourceManager;
    FirebaseUploader firebaseUploader;

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

    private ResourceListener upvoteListener;

    public GameCaptionViewAdapter(List<Caption> items) {
        this.items = new ArrayList<>(items);
        firebaseResourceManager = new FirebaseResourceManager();
        firebaseUploader = new FirebaseUploader();
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

        upvoteListener = new ResourceListener<List<Map<String, Integer>>>() {
            @Override
            public void onData(List<Map<String, Integer>> upvotes) {
                if (upvotes != null) {
                    // Set upvotes to be the list of upvotes;
                    holder.numberUpvotes.setText(String.format(Locale.getDefault(),
                            "%d", upvotes.size()));
                    System.out.println("onData ran " + upvotes.size());
                    //TODO find out how to get the data back
                }
            }

            @Override
            public Class getDataType() {
                return List.class;
            }
        };

        firebaseResourceManager.retrieveAllWithUpdates(String.format(UPVOTES_PATH, caption.getGameId(), caption.getId()), upvoteListener);
        //TODO find out how to get the upvotes to start

        holder.captionText.setText(caption.retrieveCaptionText());

        holder.numberUpvotes.setText(String.format(Locale.getDefault(),
                "%d", caption.retrieveNumVotes()));
        holder.upvote.setOnClickListener(new UpvoteClickListener(caption));
        if (caption.hasUpvoted(FirebaseResourceManager.getUserId())) {
            holder.upvote.setImageDrawable(holder.upvote.getResources().getDrawable(R.drawable.thumbs_up_filled));
        }
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

    private void handleClickUpvote(final ImageView upvote, Caption caption) {
        //Using the deprecated method because the current version isn't compatible with our min API
        //TODO check if the user has upvoted the caption already
        if (caption.hasUpvoted(FirebaseResourceManager.getUserId())) {
            upvote.setImageDrawable(upvote.getResources().getDrawable(R.drawable.thumbs_up_blank));
            caption.removeUpvote(FirebaseResourceManager.getUserId());
        }
        else {
            upvote.setImageDrawable(upvote.getResources().getDrawable(R.drawable.thumbs_up_filled));
            caption.addUpvote(FirebaseResourceManager.getUserId());
            firebaseUploader.addUpvote(caption.getId(), FirebaseResourceManager.getUserId(), caption.getUserId(), caption.getGameId(), new Uploader.UploadListener() {
                @Override
                public void onComplete() {}

                @Override
                public void onError(String errorMessage) {
                    upvote.setImageDrawable(upvote.getResources().getDrawable(R.drawable.thumbs_up_filled));
                    //TODO do something with toast that says the upvote didn't go through
                }
            });
        }
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
