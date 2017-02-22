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

import com.snaptiongame.snaptionapp.CreateGameActivity;
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

    private class UpvoteClickListener implements View.OnClickListener {
        Caption caption;
        boolean hasUpvoted;

        public UpvoteClickListener(Caption caption, boolean hasUpvoted) {
            this.caption = caption;
            this.hasUpvoted = hasUpvoted;
        }

        @Override
        public void onClick(View upvote) {
            handleClickUpvote((ImageView) upvote, caption, hasUpvoted);
        }
    }

    public GameCaptionViewAdapter(List<Caption> items) {
        this.items = new ArrayList<>(items);
    }

    /**
     * Sets the text, images, and click handlers for the Caption view
     *
     * @param holder The object that contains the views to display
     * @param position The position in the list of Captions
     */
    @Override
    public void onBindViewHolder(final CaptionViewHolder holder, int position) {
        final Caption caption = items.get(position);
        FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();

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

        // Listens for any changes to the upvotes, modifies the upvote icon, number of upvotes,
        // and modifies the click handler
        ResourceListener upvoteListener = new ResourceListener<Map<String, Integer>>() {
            @Override
            public void onData(Map<String, Integer> upvotes) {
                if (upvotes != null) {
                    // Set upvotes to be the list of upvotes;
                    holder.numberUpvotesText.setText(String.format(Locale.getDefault(),
                            "%d", upvotes.size()));
                    // Sets the click listener, which changes implementation depending on upvote status
                    holder.upvoteIcon.setOnClickListener(new UpvoteClickListener(caption,
                            upvotes.containsKey(FirebaseResourceManager.getUserId())));
                    // Sets the icon depending on whether it has been upvoted
                    setUpvoteIcon(holder.upvoteIcon,
                            upvotes.containsKey(FirebaseResourceManager.getUserId()));
                }
                else {
                    holder.numberUpvotesText.setText("0");
                    holder.upvoteIcon.setOnClickListener(new UpvoteClickListener(caption, false));
                    setUpvoteIcon(holder.upvoteIcon, false);
                }
            }

            @Override
            public Class getDataType() {
                return Map.class;
            }
        };

        holder.numberUpvotesText.setText(String.format(Locale.getDefault(), "%d",
                caption.getVotes() == null ? 0 : caption.getVotes().size()));
        holder.upvoteIcon.setOnClickListener(new UpvoteClickListener(caption, false));
        setUpvoteIcon(holder.upvoteIcon, false);

        //Gets the map of upvotes and configures it to call the upvote listener whenever it is modified
        firebaseResourceManager.retrieveMapWithUpdates(String.format(UPVOTES_PATH,
                caption.getGameId(), caption.getId()), upvoteListener, Integer.class);

        holder.captionText.setText(caption.retrieveCaptionText());

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

    private void handleClickUpvote(final ImageView upvoteIcon, Caption caption, boolean hasUpvoted) {
        //Using the deprecated method because the current version isn't compatible with our min API
        //TODO check if the user has upvoted the caption already
        Uploader uploader = new FirebaseUploader();

        if (hasUpvoted) {
            Toast.makeText(upvoteIcon.getContext(), "This will remove the upvote", Toast.LENGTH_SHORT).show();
        }
        else {
            if (caption != null) {
                caption.addUpvote(FirebaseResourceManager.getUserId());
                uploader.addUpvote(caption.getId(), FirebaseResourceManager.getUserId(),
                    caption.getUserId(), caption.getGameId(), new Uploader.UploadListener() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(upvoteIcon.getContext(), "There was an error in registering " +
                                "your vote.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void setUpvoteIcon(ImageView upvoteIcon, boolean hasUpvoted) {
        if (hasUpvoted) {
            //Using the deprecated method because the current version isn't compatible with our min API
            upvoteIcon.setImageDrawable(upvoteIcon.getResources().getDrawable(R.drawable.thumbs_up_filled));
        }
        else {
            upvoteIcon.setImageDrawable(upvoteIcon.getResources().getDrawable(R.drawable.thumbs_up_blank));
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
