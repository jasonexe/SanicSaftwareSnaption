package com.snaptiongame.snaption.ui.games;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.login.LoginDialog;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.GAME_CAPTION_PATH;

/**
 * Provides a binding for captions to be displayed using a RecyclerView in GameActivity.
 * Handles Firebase interactions with upvotes and dynamically reloads captions based on changes.
 *
 * @author Cameron Geehr
 */
public class GameCaptionViewAdapter extends RecyclerView.Adapter<CaptionViewHolder> {

    private static final String UNKNOWN_USER_ID = "unknown";
    private List<Caption> items;
    private LoginDialog loginDialog;
    private ProfileActivity.ProfileActivityCreator profileMaker;
    private User unknownUser = new User(UNKNOWN_USER_ID, null, null, null, null, null);

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
    public GameCaptionViewAdapter(List<Caption> items, LoginDialog loginDialog,
                                  ProfileActivity.ProfileActivityCreator profileMaker) {
        this.items = items;
        Collections.sort(this.items);
        this.loginDialog = loginDialog;
        this.profileMaker = profileMaker;
    }

    /**
     * Sets the text, images, and click handlers for the Caption view.
     *
     * @param holder The object that contains the views to display
     * @param position The position in the list of Captions
     */
    @Override
    public void onBindViewHolder(final CaptionViewHolder holder, int position) {
        final Caption finalCaption = items.get(position);
        setUpCaptionView(holder, finalCaption);

        // Get information about the captioner if it is not already present
        if (finalCaption.retrieveUser() == null) {
            String userPath = FirebaseResourceManager.getUserPath(finalCaption.getUserId());
            FirebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
//                    int captionNdx = items.indexOf(finalCaption);
//                    Caption captionInList = items.get(captionNdx);
                    // if the user could not be found, set user to unknown user
                    if (user == null) {
                        user = unknownUser;
                    }
//                    captionInList.assignUser(user);
//                    notifyItemChanged(captionNdx);
                    setCaptionerView(user, holder);
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    public void captionChanged(Caption updatedCaption) {
        if (updatedCaption != null) {
            int oldIndex, newIndex;
            oldIndex = items.indexOf(updatedCaption);
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
        upvoteIcon.setImageDrawable(hasUpvoted ?
                ContextCompat.getDrawable(upvoteIcon.getContext(), R.drawable.thumb_up) :
                ContextCompat.getDrawable(upvoteIcon.getContext(), R.drawable.thumb_up_outline));
    }

    private void setUpCaptionView(CaptionViewHolder holder, Caption caption) {
        holder.captionText.setText(caption.retrieveCaptionText());
//        setCaptionerView(caption.retrieveUser(), holder);
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
        // Set upvoteCountText to be the the number of upvotes;
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
    private void setCaptionerView(final User captioner, CaptionViewHolder holder) {
        System.err.println("Setting up with user " + captioner);
        if (captioner != null) {
            // Set the default captioner info if unknown user
            if (captioner.equals(unknownUser)) {
                holder.captionerName.setText(holder.captionerName
                        .getContext().getResources().getString(R.string.null_user));
                holder.captionerPhoto.setImageDrawable(ContextCompat.getDrawable(
                        holder.captionerPhoto.getContext(),
                        R.drawable.com_facebook_profile_picture_blank_square));
                holder.captionerPhoto.setOnClickListener(null);
            }
            // Display the captioner info if valid user
            else {
                holder.captionerName.setText(captioner.getDisplayName());
                FirebaseResourceManager.loadImageIntoView(captioner.getImagePath(),
                        holder.captionerPhoto);
                //set click listener to go to user's profile
                holder.captionerPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profileMaker.create(captioner.getId());
                    }
                });
            }
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