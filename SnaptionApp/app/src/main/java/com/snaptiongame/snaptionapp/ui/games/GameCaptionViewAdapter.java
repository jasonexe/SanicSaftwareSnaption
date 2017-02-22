package com.snaptiongame.snaptionapp.ui.games;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides a binding for captions to be displayed using a RecyclerView in GameActivity.
 *
 * @author Cameron Geehr
 */

public class GameCaptionViewAdapter extends RecyclerView.Adapter<CaptionViewHolder> {

    private List<Caption> items;
    private LoginDialog loginDialog;
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
            // Check if user is logged in before letting them upvote. If not logged in, display
            // login dialog.
            if(FirebaseResourceManager.getUserId() == null) {
                loginDialog.show();
            } else {
                handleClickUpvote((ImageView) upvote, caption, hasUpvoted);
            }
        }
    }

    /**
     * Creates an instance of this GameCaptionViewAdapter.
     *
     * @param items The list of Captions to build the views from
     */
    public GameCaptionViewAdapter(List<Caption> items, LoginDialog loginDialog) {
        this.items = new ArrayList<>(items);
        this.loginDialog = loginDialog;
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
                    FirebaseResourceManager.loadImageIntoView(user.getImagePath(),
                            holder.captionerPhoto);
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
                    setDefaultUpvoteView(holder, caption);
                }
            }

            @Override
            public Class getDataType() {
                return Integer.class;
            }
        };

        setDefaultUpvoteView(holder, caption);
        holder.captionText.setText(caption.retrieveCaptionText());

        //Gets the map of upvotes and configures it to call the upvote listener whenever it is modified
        firebaseResourceManager.retrieveMapWithUpdates(String.format(UPVOTES_PATH,
                caption.getGameId(), caption.getId()), upvoteListener);
    }

    /**
     * Sets the initial/default view for the upvote icon and upvote number, and sets the click
     * listener for the upvote icon.
     *
     * @param holder The holder for the view
     * @param caption The caption being affected
     */
    private void setDefaultUpvoteView(CaptionViewHolder holder, Caption caption) {
        // Using 0 because it will be replaced immediately and using the local variable results in
        // some bugs
        holder.numberUpvotesText.setText(String.format(Locale.getDefault(), "%d", 0));
        holder.upvoteIcon.setOnClickListener(new UpvoteClickListener(caption, false));
        setUpvoteIcon(holder.upvoteIcon, false);
    }

    @Override
    public CaptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_caption_item,
                parent, false);
        return new CaptionViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    /**
     * Called when an upvote is clicked. Adds/removes the upvote to/from firebase depending on
     * whether hasUpvoted is true or false.
     *
     * @param upvoteIcon The view being clicked
     * @param caption The caption object being affected
     * @param hasUpvoted Whether the user has previously upvoted this caption
     */
    private void handleClickUpvote(final ImageView upvoteIcon, Caption caption,
                                   boolean hasUpvoted) {
        // Listens to see if anything went wrong
        Uploader.UploadListener listener = new Uploader.UploadListener() {
            @Override
            public void onComplete() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(upvoteIcon.getContext(),
                        upvoteIcon.getContext().getResources().getString(R.string.upvote_error),
                        Toast.LENGTH_SHORT).show();
            }
        };

        if (caption != null) {
            // Remove the upvote if the user has upvoted
            if (hasUpvoted) {
                FirebaseUploader.removeUpvote(caption.getId(), FirebaseResourceManager.getUserId(),
                        caption.getUserId(), caption.getGameId(), listener);
            }
            // Add the upvote if the user hasn't upvoted
            else {
                FirebaseUploader.addUpvote(caption.getId(), FirebaseResourceManager.getUserId(),
                        caption.getUserId(), caption.getGameId(), listener);
            }
        }
    }

    /**
     * Sets the upvote icon to be either filled or empty depending on whether the user has upvoted
     * the caption.
     *
     * @param upvoteIcon The view being affected
     * @param hasUpvoted Whether the user has upvoted the caption
     */
    private void setUpvoteIcon(ImageView upvoteIcon, boolean hasUpvoted) {
        if (hasUpvoted) {
            //Using the deprecated method because the current version isn't compatible with our min API
            upvoteIcon.setImageDrawable(upvoteIcon.getResources()
                    .getDrawable(R.drawable.thumbs_up_filled));
        }
        else {
            upvoteIcon.setImageDrawable(upvoteIcon.getResources()
                    .getDrawable(R.drawable.thumbs_up_blank));
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
