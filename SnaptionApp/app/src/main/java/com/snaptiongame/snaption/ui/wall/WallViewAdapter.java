package com.snaptiongame.snaption.ui.wall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.MainSnaptionActivity;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.new_game.CreateGameActivity;
import com.snaptiongame.snaption.utilities.ViewUtilities;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.GAME_PRIVATE_METADATA_UPVOTE_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_METADATA_UPVOTE_PATH;
import static com.snaptiongame.snaption.ui.profile.ProfileActivity.*;

/**
 * The adapter for Games to be displayed in the wall's recyclerview.
 * Handles creating a new game, viewing a game, and upvoting games.
 *
 * @author Brittany Berlanga
 * @author Cameron Geehr
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {
    private static final int UPVOTE_ANIM_DIST = 40;
    private static final int UPVOTE_ANIM_DURATION = 800;
    private static final float UPVOTE_ANIM_SCALE = 0.5f;
    private List<GameMetadata> items;
    private Map<Integer, WallViewHolder> itemNumToHolder;
    private ProfileActivityCreator profileMaker;
    private OnClickGamePhotoListener onClickGamePhotoListener;

    public WallViewAdapter(List<GameMetadata> items, ProfileActivityCreator profileMaker) {
        this.items = items;
        this.profileMaker = profileMaker;
        itemNumToHolder = new HashMap<>();
    }

    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_game_item, parent, false);
        WallViewHolder holder = new WallViewHolder(view);
        holder.photo.setMaxImageHeight(parent.getContext().getResources()
                .getDimension(R.dimen.max_wall_image_height));
        return holder;
    }

    public interface OnClickGamePhotoListener {
        void onClickGamePhoto(View view, GameMetadata game);
    }

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {
        final GameMetadata game = items.get(position);

        // set the height of the game photo with the image aspect ratio
        holder.photo.setImageAspectRatio(game.getImageAspectRatio());

        // display the Picker of the game, the one who created it
        displayUser(holder.pickerName, null, game.getPickerId());

        // ensure the game has a top caption before displaying the caption and the captioner
        displayCaption(holder, game);

        Map<String, Integer> upvoters = game.getUpvotes();
        if(upvoters != null) {
            setUpvoteView(holder, game, upvoters.size(), upvoters.containsKey(FirebaseUserResourceManager.getUserId()), false);
        } else {
            setUpvoteView(holder, game, 0, false, false);
        }
        itemNumToHolder.put(position, holder);

        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickGamePhotoListener != null) {
                    onClickGamePhotoListener.onClickGamePhoto(holder.photo, game);
                }
            }
        });
        ViewCompat.setTransitionName(holder.photo, game.getId());

        // distinguish between complete and incomplete games, and public/private
        setCaptionTextStyle(holder, game);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // allows the image to be clipped with rounded edges
            holder.photo.setClipToOutline(true);
        }

        final String imagePath = game.getImagePath();
        // TODO add a confirmation dialog that they want to create the new game??
        holder.photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                //if the user is logged in
                if (FirebaseUserResourceManager.getUserId() != null) {
                    // If they want to create a game from this one, start the create game intent
                    // with this game's image path
                    FirebaseResourceManager.getImageURI(imagePath, new ResourceListener<Uri>() {
                        @Override
                        public void onData(Uri data) {
                            Context buttonContext = view.getContext();
                            Intent createGameIntent = new Intent(buttonContext, CreateGameActivity.class);
                            createGameIntent.putExtra(Constants.EXTRA_MESSAGE, data);
                            createGameIntent.putExtra(Constants.PHOTO_PATH, imagePath);
                            buttonContext.startActivity(createGameIntent);
                        }

                        @Override
                        public Class getDataType() {
                            return Uri.class;
                        }
                    });
                }
                else { //prompt user to log in
                    ((MainSnaptionActivity) view.getContext()).loginDialog.show();
                }
                return true;
            }
        });
    }

    /**
     * Sets the upvote icon to be either filled or empty depending on whether the user has upvoted
     * the game.
     *
     * @param holder WallViewHolder containing the upvote icon
     * @param hasUpvoted Whether the user has upvoted the game
     * @param animate Whether the upvote icon should show a "ghost" animation
     */
    private void setUpvoteIcon(final WallViewHolder holder, boolean hasUpvoted, boolean animate) {
        ImageView upvoteIcon = holder.upvoteIcon;
        upvoteIcon.setImageDrawable(hasUpvoted ?
                ContextCompat.getDrawable(upvoteIcon.getContext(), R.drawable.thumb_up) :
                ContextCompat.getDrawable(upvoteIcon.getContext(), R.drawable.thumb_up_outline));
        if (animate) {
            ViewUtilities.animateGhost(holder.gameInfoLayout, upvoteIcon, UPVOTE_ANIM_DIST,
                    UPVOTE_ANIM_DURATION, UPVOTE_ANIM_SCALE);
        }
    }

    /**
     * Sets the initial/default view for the upvote icon and upvote number, and sets the click
     * listener for the upvote icon.
     *
     * @param holder The holder for the view
     * @param game The game being affected
     * @param numUpvotes number of game upvotes
     * @param hasUpvoted whether the user has upvoted the game
     * @param animate whether the upvote icon should show a "ghost" animation
     */
    private void setUpvoteView(WallViewHolder holder, final GameMetadata game, int numUpvotes,
                               final boolean hasUpvoted, boolean animate) {
        // Set upvoteCountText to be the the number of upvotes;
        holder.upvoteCountText.setText(NumberFormat.getInstance().format(numUpvotes));
        // Sets the click listener, which changes implementation depending on upvote status
        final Context context = holder.itemView.getContext();
        holder.upvoteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in before letting them upvote. If not logged in, display
                // login dialog.
                if (FirebaseUserResourceManager.getUserId() == null) {
                    ((MainSnaptionActivity) context).loginDialog.show();
                }
                else {
                    handleClickUpvote(context, game, hasUpvoted);
                }
            }
        });
        // Sets the icon depending on whether it has been upvoted
        setUpvoteIcon(holder, hasUpvoted, animate);
    }

    private void displayCaption(WallViewHolder holder, GameMetadata game) {
        if (game.getTopCaption() != null) {
            holder.captionerLayout.setVisibility(TextView.VISIBLE);
            holder.captionText.setText(game.getTopCaption().retrieveCaptionText());
            displayUser(holder.captionerName, holder.captionerPhoto, game.getTopCaption().getUserId());
        }
        else {
            // display a request to participate over the caption's view if a caption does not exist
            holder.captionText.setText(R.string.caption_filler);
            holder.captionerLayout.setVisibility(TextView.GONE);
        }
    }

    /**
     * Sets the caption text based on whether the game is public/private, closed/open.
     *
     * @param holder The viewholder containing the caption text
     * @param game The game object being referenced
     */
    private void setCaptionTextStyle(WallViewHolder holder, GameMetadata game) {
        if (!game.getIsPublic() && !game.isOpen()) {
            // If private and closed, bold italic
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.BOLD_ITALIC);
        } else if (game.isOpen() && game.getIsPublic()) {
            // If open and public, normal
            holder.captionText.setTypeface(Typeface.create(holder.captionText.getTypeface(),
                    Typeface.NORMAL), Typeface.NORMAL);
        } else if (!game.isOpen()) {
            // If just closed, bold
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.BOLD);
        } else {
            // If private, italicize
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.ITALIC);
        }
    }

    @Override
    public void onViewRecycled(WallViewHolder vh) {
        int position = vh.getAdapterPosition();
        itemNumToHolder.remove(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Displays the profile picture and username of a valid User. Shows the default if invalid
     * @param username the TextView from the holder, for either the Picker or Captioner's name
     * @param photo the ImageView from the holder, for either the Picker or Captioner's photo
     * @param userId The id of the current user
     */
    private void displayUser(final TextView username, final ImageView photo, String userId) {
        // remove this portion if firebase is guaranteed to not have invalid users
        username.setText(" ");
        if (photo != null) {
            Glide.with(photo.getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(photo);
        }

        // ensure the user id is a valid one to avoid errors
        if (FirebaseUserResourceManager.isValidUser(userId)) {
            // display the name and profile picture if a valid user is obtained from the user id
            FirebaseUserResourceManager.getUserMetadataById(userId, new ResourceListener<UserMetadata>() {
                @Override
                public void onData(final UserMetadata user) {
                    // replace default is the User is valid
                    if (user != null) {
                        // if there is no photo path, don't display it, use a - instead
                        if (photo != null) {
                            username.setText(user.getDisplayName());
                            FirebaseResourceManager.loadImageIntoView(user.getImagePath(), photo);
                            photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //call create user profile activity
                                    profileMaker.create(user.getId());
                                }
                            });
                        }
                        else {
                            username.setText(user.getDisplayName());
                        }

                    }
                }

                @Override
                public Class getDataType() {
                    return UserMetadata.class;
                }
            });
        }
    }

    /**
     * Adds a list of games.
     *
     * @param newGames a list of games to add
     */
    public void addItems(List<GameMetadata> newGames) {
        int startPos = items.size();
        items.addAll(newGames);
        this.notifyItemRangeChanged(startPos, newGames.size());
    }

    public void gameChanged(int changedIndex, Map<String, Integer> newUpvotes) {
        GameMetadata newGame = items.get(changedIndex);
        Map<String, Integer> oldUpvotes = newGame.getUpvotes();
        newGame.setUpvotes(newUpvotes);
        WallViewHolder holder = itemNumToHolder.get(changedIndex);

        if(holder != null) {
            if (newUpvotes == null) {
                setUpvoteView(holder, newGame, 0, false, false);
            } else {
                String userId = FirebaseUserResourceManager.getUserId();
                boolean hasUpvoted = newUpvotes.containsKey(userId);
                boolean hasUpvotedPrior = oldUpvotes != null && oldUpvotes.containsKey(userId);
                setUpvoteView(holder, newGame, newUpvotes.size(), hasUpvoted, hasUpvoted
                        && !hasUpvotedPrior);
            }
        }
    }

    /**
     * Called when an upvote is clicked. Adds/removes the upvote to/from firebase depending on
     * whether hasUpvoted is true or false.
     *
     * @param game The game object being affected
     * @param hasUpvoted Whether the user has previously upvoted this game
     */
    private void handleClickUpvote(final Context context, GameMetadata game,
                                   boolean hasUpvoted) {
        // Listens to see if anything went wrong
        Uploader.UploadListener listener = new Uploader.UploadListener() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, context.getResources().getString(R.string.upvote_error),
                        Toast.LENGTH_SHORT).show();
            }
        };

        if (game != null) {
            String upvotePath = game.getIsPublic() ?
                    String.format(GAME_PUBLIC_METADATA_UPVOTE_PATH, game.getId(),
                            FirebaseUserResourceManager.getUserId()) :
                    String.format(GAME_PRIVATE_METADATA_UPVOTE_PATH, game.getId(),
                            FirebaseUserResourceManager.getUserId());
            // Remove the upvote if the user has upvoted
            if (hasUpvoted) {
                FirebaseUploader.removeUpvote(upvotePath, listener);
            }
            // Add the upvote if the user hasn't upvoted
            else {
                FirebaseUploader.addUpvote(upvotePath, listener);
            }
        }
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setOnClickGamePhotoListener(OnClickGamePhotoListener onClickGamePhotoListener) {
        this.onClickGamePhotoListener = onClickGamePhotoListener;
    }
}
