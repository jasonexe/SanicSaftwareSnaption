package com.snaptiongame.snaptionapp.ui.games;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaptionapp.Constants.GAME_CAPTION_PATH;

/**
 * Provides a binding for captions to be displayed using a RecyclerView in GameActivity.
 * Handles Firebase interactions with upvotes and dynamically reloads captions based on changes.
 *
 * @author Cameron Geehr
 */
public class GameCaptionViewAdapter extends RecyclerView.Adapter<CaptionViewHolder> {

    private List<Caption> items;
    private LoginDialog loginDialog;
    // Holds the FirebaseResourceManagers to prevent them from having to be re-created and prevents memory leaks
    protected Map<String, FirebaseResourceManager> resourceManagerMap;

    // BEGIN PRIVATE CLASSES //

    /**
     * The click listener for the upvote button.
     */
    private class UpvoteClickListener implements View.OnClickListener {
        Caption caption;
        boolean hasUpvoted;

        /**
         * Constructs the upvote click listener.
         *
         * @param caption The caption to listen to
         * @param hasUpvoted Whether the current user has upvoted the caption
         */
        public UpvoteClickListener(Caption caption, boolean hasUpvoted) {
            this.caption = caption;
            this.hasUpvoted = hasUpvoted;
        }

        @Override
        public void onClick(View upvote) {
            // Check if user is logged in before letting them upvote. If not logged in, display
            // login dialog.
            if (FirebaseResourceManager.getUserId() == null) {
                loginDialog.show();
            }
            else {
                handleClickUpvote((ImageView) upvote, caption, hasUpvoted);
            }
        }
    }

    // END PRIVATE CLASSES //

    // BEGIN PUBLIC METHODS //

    /**
     * Creates an instance of this GameCaptionViewAdapter.
     *
     * @param items The list of Captions to build the views from
     * @param loginDialog The dialog to display when the user needs to log in
     */
    public GameCaptionViewAdapter(List<Caption> items, LoginDialog loginDialog) {
        this.items = items;
        Collections.sort(this.items);
        this.loginDialog = loginDialog;
        resourceManagerMap = new HashMap<>(items.size());
    }

    /**
     * Sets the text, images, and click handlers for the Caption view.
     *
     * @param holder The object that contains the views to display
     * @param position The position in the list of Captions
     */
    @Override
    public void onBindViewHolder(CaptionViewHolder holder, int position) {
        final Caption finalCaption = items.get(position);
        setUpCaptionView(holder, finalCaption);

        // Adds a firebase resource manager for the caption to the map if one is not already in it
        if (!resourceManagerMap.containsKey(finalCaption.getId())) {
            FirebaseResourceManager manager = new FirebaseResourceManager();
            // Listens for any changes to the upvotes and updates the caption
            ResourceListener upvoteListener = new ResourceListener<Caption>() {
                @Override
                public void onData(Caption updatedCaption) {
                    if (updatedCaption != null) {
                        int oldIndex, newIndex;
                        oldIndex = items.indexOf(finalCaption);
                        Caption captionInList = items.get(oldIndex);
                        items.remove(oldIndex);
                        updatedCaption.assignUser(captionInList.retrieveUser());
                        newIndex = insertCaption(updatedCaption);
                        // Check to see if the caption has moved or changed, and if it has
                        // then animate its change
                        if (oldIndex != newIndex) {
                            notifyItemMoved(oldIndex, newIndex);
                            notifyItemChanged(newIndex);
                        } else {
                            notifyItemChanged(oldIndex);
                        }
                    }
                }

                @Override
                public Class getDataType() {
                    return Caption.class;
                }
            };

            //Configures resource manager to call the upvote listener whenever the caption is modified
            manager.retrieveSingleWithUpdates(String.format(GAME_CAPTION_PATH,
                    finalCaption.getGameId(), finalCaption.getId()), upvoteListener);
            resourceManagerMap.put(finalCaption.getId(), manager);
        }

        // Get information about the captioner if it is not already present
        if (finalCaption.retrieveUser() == null) {
            String userPath = FirebaseResourceManager.getUserPath(finalCaption.getUserId());
            FirebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    int captionNdx = items.indexOf(finalCaption);
                    Caption captionInList = items.get(captionNdx);
                    captionInList.assignUser(user);
                    notifyItemChanged(captionNdx);
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    /**
     * Creates the caption view holder.
     *
     * @param parent The parent to display it in
     * @param viewType
     * @return The caption view holder
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
        // Only add the caption if it isn't already in the list
        if (!items.contains(newCaption)) {
            items.add(newCaption);
            this.notifyItemInserted(items.size() - 1);
        }
    }

    public void removeListeners() {
        for (FirebaseResourceManager frm : resourceManagerMap.values()) {
            frm.removeListener();
        }
        resourceManagerMap.clear();
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
            // Because the listener is being used twice, don't display the error if it has already appeared
            boolean hasDisplayedError = false;
            @Override
            public void onComplete() {}

            @Override
            public void onError(String errorMessage) {
                if (!hasDisplayedError) {
                    Toast.makeText(upvoteIcon.getContext(),
                            upvoteIcon.getContext().getResources().getString(R.string.upvote_error),
                            Toast.LENGTH_SHORT).show();
                    hasDisplayedError = true;
                }
            }
        };

        if (caption != null) {
            // Remove the upvote if the user has upvoted
            if (hasUpvoted) {
                FirebaseUploader.removeUpvote(
                        String.format(Constants.GAME_CAPTIONS_UPVOTE_PATH, caption.getGameId(),
                                caption.getId(), FirebaseResourceManager.getUserId()), listener);
                FirebaseUploader.removeUpvote(
                        String.format(Constants.USER_CAPTIONS_UPVOTE_PATH, caption.getUserId(),
                                caption.getId(), FirebaseResourceManager.getUserId()), listener);
            }
            // Add the upvote if the user hasn't upvoted
            else {
                FirebaseUploader.addUpvote(
                        String.format(Constants.GAME_CAPTIONS_UPVOTE_PATH, caption.getGameId(),
                            caption.getId(), FirebaseResourceManager.getUserId()), listener);
                FirebaseUploader.addUpvote(String.format(Constants.USER_CAPTIONS_UPVOTE_PATH,
                        caption.getUserId(), caption.getId(), FirebaseResourceManager.getUserId()),
                        listener);
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

    private void setUpCaptionView(CaptionViewHolder holder, Caption caption) {
        holder.captionText.setText(caption.retrieveCaptionText());
        setCaptionerView(caption.retrieveUser(), holder);
        // Set the display to reflect the status of the caption
        if (caption.votes != null) {
            Map votes = caption.votes;
            setUpvoteView(holder, caption, votes.size(),
                    votes.containsKey(FirebaseResourceManager.getUserId()));
        }
        else {
            setUpvoteView(holder, caption, 0, false);
        }
    }

    /**
     * Sets the initial/default view for the upvote icon and upvote number, and sets the click
     * listener for the upvote icon.
     *
     * @param holder The holder for the view
     * @param caption The caption being affected
     */
    private void setUpvoteView(CaptionViewHolder holder, Caption caption, int numUpvotes,
                               boolean hasUpvoted) {
        // Set numberUpvotesText to be the the number of upvotes;
        holder.numberUpvotesText.setText(NumberFormat.getInstance().format(numUpvotes));
        // Sets the click listener, which changes implementation depending on upvote status
        holder.upvoteIcon.setOnClickListener(new UpvoteClickListener(caption, hasUpvoted));
        // Sets the icon depending on whether it has been upvoted
        setUpvoteIcon(holder.upvoteIcon, hasUpvoted);
    }

    /**
     * Displays the information about the user who made the caption.
     *
     * @param captioner The user who made the caption
     * @param holder The view holder containing the caption information
     */
    private void setCaptionerView(User captioner, CaptionViewHolder holder) {
        // Display the captioner's information
        if (captioner != null) {
            holder.captionerName.setText(captioner.getDisplayName());
            FirebaseResourceManager.loadImageIntoView(captioner.getImagePath(),
                    holder.captionerPhoto);
        }
        // Set the default view for a user
        else {
            holder.captionerName.setText(holder.captionerName
                    .getContext().getResources().getString(R.string.null_user));
            holder.captionerPhoto.setImageDrawable(holder.captionerPhoto.getContext()
                    .getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
        }
    }

    /**
     * Inserts the caption into the items list in the proper order.
     *
     * @param caption The caption to insert
     */
    private int insertCaption(Caption caption) {
        int index = 0;
        boolean added = false;

        while (index < items.size() && !added) {
            if (caption.compareTo(items.get(index)) < 0) {
                items.add(index, caption);
                added = true;
            }
            else {
                index++;
            }
        }
        if (!added) {
            items.add(caption);
        }
        return index;
    }

}