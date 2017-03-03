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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    private FirebaseResourceManager firebaseResourceManager;
    private static final String CAPTIONS_PATH = "games/%s/captions";

    // BEGIN PRIVATE CLASSES //

    /**
     * The click listener for the upvote button.
     */
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
     * A listener for updating captions.
     *
     * @param <T> It's Caption. Don't use something else.
     */
    private class CaptionListener<T> implements ResourceListener<T> {
        public Class getDataType() {
            return Caption.class;
        }

        public void onData(T data) {
            if (data != null) {
                HashMap<String, Caption> captionMap = (HashMap<String, Caption>) data;
                items = new ArrayList<>(captionMap.values());
                refreshView();
            }
        }
    }

    /**
     * Compares two caption objects for order based on the number of votes. Used for ordering.
     *
     * @param <T> It's Caption. Don't use something other than Caption.
     */
    private class CaptionComparator<T> implements Comparator<T> {
        public int compare(T a, T b) {
            Map<String, Integer> aVotes = ((Caption)a).getVotes();
            Map<String, Integer> bVotes = ((Caption)b).getVotes();
            int aVotesSize = 0;
            int bVotesSize = 0;

            if (aVotes != null) {
                aVotesSize = aVotes.size();
            }
            if (bVotes != null) {
                bVotesSize = bVotes.size();
            }
            // If there's a tie in the vote count it will sort based on date
            return bVotesSize == aVotesSize ? ((Caption)b).getId().compareTo(((Caption)b).getId()) :
                    bVotesSize - aVotesSize;
        }
    }

    // END PRIVATE CLASSES //

    // BEGIN PUBLIC METHODS //

    /**
     * Creates an instance of this GameCaptionViewAdapter.
     *
     * @param gameId The game to pull captions from
     * @param loginDialog The dialog to display if the user isn't logged in
     */
    public GameCaptionViewAdapter(String gameId, LoginDialog loginDialog) {
        this.items = new ArrayList<>();
        //Collections.sort(this.items, new CaptionComparator<Caption>());
        this.loginDialog = loginDialog;
        firebaseResourceManager = new FirebaseResourceManager();

        CaptionListener<Caption> captionListener = new CaptionListener<>();
        //Gets the map of captions and configures it to call the caption listener whenever it is modified
        firebaseResourceManager.retrieveMapWithUpdates(String.format(CAPTIONS_PATH,
                gameId), captionListener);
    }

    /**
     * Sets the text, images, and click handlers for the Caption view
     *
     * @param holder The object that contains the views to display
     * @param position The position in the list of Captions
     */
    @Override
    public void onBindViewHolder(final CaptionViewHolder holder, final int position) {
        final Caption caption = items.get(position);

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

        if (caption.getVotes() == null) {
            setDefaultUpvoteView(holder, caption);
        }
        else {
            holder.numberUpvotesText.setText(String.format(Locale.getDefault(),
                    "%d", caption.getVotes().size()));
            // Sets the click listener, which changes implementation depending on upvote status
            holder.upvoteIcon.setOnClickListener(new UpvoteClickListener(caption,
                    caption.getVotes().containsKey(FirebaseResourceManager.getUserId())));
            // Sets the icon depending on whether it has been upvoted
            setUpvoteIcon(holder.upvoteIcon,
                    caption.getVotes().containsKey(FirebaseResourceManager.getUserId()));
        }
        holder.captionText.setText(caption.retrieveCaptionText());
    }

    /**
     * Creates the caption view holder.
     *
     * @param parent The parent to display it in
     * @param viewType
     * @return the caption view holder
     */
    @Override
    public CaptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_caption_item,
                parent, false);
        return new CaptionViewHolder(view);
    }

    /**
     * Gets the number of items in the list.
     *
     * @return the number of items
     */
    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    /**
     * Adds more captions into the list of captions.
     *
     * @param newCaption A caption to add to the list
     */
    public void addCaption(Caption newCaption) {
        if (!items.contains(newCaption)) {
            items.add(newCaption);
        }
        this.notifyDataSetChanged();
    }

    // END PUBLIC METHODS //

    // BEGIN PRIVATE METHODS //

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
     * Re-sorts the list and updates the view.
     */
    private void refreshView() {
        Collections.sort(items, new CaptionComparator<Caption>());
        notifyDataSetChanged();
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

}
