package com.snaptiongame.snaptionapp.ui.wall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.ui.new_game.CreateGameActivity;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.validFirebasePath;

/**
 * The adapter for Games to be displayed in the wall's recyclerview.
 * Handles creating a new game, viewing a game, and upvoting games.
 *
 * @author Brittany Berlanga
 * @author Cameron Geehr
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {

    private List<Game> items;
    private MainSnaptionActivity activity;
    // Holds the firebaseresourcemanagers to prevent them from having to be re-created and prevents memory leaks
    protected Map<String, FirebaseResourceManager> resourceManagerMap;

    public WallViewAdapter(List<Game> items, MainSnaptionActivity activity) {
        this.items = items;
        this.activity = activity;
        resourceManagerMap = new HashMap<>(items.size());
    }

    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_game_item, parent, false);
        return new WallViewHolder(view);
    }

    class PhotoClickListener implements View.OnClickListener {
        Game game;
        public PhotoClickListener(Game game) {
            this.game = game;
        }

        @Override
        public void onClick(View view) {
            Context imageContext = view.getContext();
            Intent createGameIntent = new Intent(imageContext, GameActivity.class);
            createGameIntent.putExtra(Constants.GAME, game);
            imageContext.startActivity(createGameIntent);
        }
    }

    /**
     * The click listener for the upvote button.
     */
    private class UpvoteClickListener implements View.OnClickListener {
        Game game;
        boolean hasUpvoted;

        /**
         * Constructs the upvote click listener.
         *
         * @param game The game to listen to
         * @param hasUpvoted Whether the current user has upvoted the caption
         */
        public UpvoteClickListener(Game game, boolean hasUpvoted) {
            this.game = game;
            this.hasUpvoted = hasUpvoted;
        }

        @Override
        public void onClick(View upvote) {
            // Check if user is logged in before letting them upvote. If not logged in, display
            // login dialog.
            if (FirebaseResourceManager.getUserId() == null) {
                activity.loginDialog.show();
            }
            else {
                handleClickUpvote((ImageView) upvote, game, hasUpvoted);
            }
        }
    }

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {
        Game game = items.get(position);

        // Adds the firebaseresourcemanager to the map if it is not already in it
        if (!resourceManagerMap.containsKey(game.getId())) {
            resourceManagerMap.put(game.getId(), new FirebaseResourceManager());
        }
        // display the Picker of the game, the one who created it
        displayUser(holder.pickerName, holder.pickerPhoto, String.format(Constants.USER_PATH, game.getPicker()));

        // ensure the game has a top caption before displaying the caption and the captioner
        displayCaption(holder, game);

        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);
        holder.photo.setOnClickListener(new PhotoClickListener(game));

        // distinguish between complete and incomplete games, and public/private
        setCaptionTextStyle(holder, game);

        if (Build.VERSION.SDK_INT >= Constants.CLIP_TO_OUTLINE_MIN_SDK) {
            // allows the image to be clipped with rounded edges
            holder.photo.setClipToOutline(true);
        }

        final String imagePath = game.getImagePath();
        // TODO add a confirmation dialog that they want to create the new game??
        holder.createFromExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the user is logged in
                if (FirebaseResourceManager.getUserId() != null) {
                    // If they want to create a game from this one, start the create game intent
                    // with this game's image path
                    FirebaseResourceManager.getImageURI(imagePath, new ResourceListener<Uri>() {
                        @Override
                        public void onData(Uri data) {
                            Context buttonContext = holder.createFromExisting.getContext();
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
                    activity.loginDialog.show();
                }
            }
        });

        // Listens for any changes to the upvotes, modifies the upvote icon, number of upvotes,
        // and modifies the click handler
        ResourceListener upvoteListener = new ResourceListener<Game>() {
            @Override
            public void onData(Game updatedGame) {
                // Check to make sure caption exists
                if (updatedGame != null) {
                    // Set the display to reflect the status of the caption
                    if (updatedGame.getVotes() != null) {
                        Map votes = updatedGame.getVotes();
                        setUpvoteView(holder, updatedGame, votes.size(),
                                votes.containsKey(FirebaseResourceManager.getUserId()));
                    }
                    else {
                        setUpvoteView(holder, updatedGame, 0, false);
                    }
                }
            }

            @Override
            public Class getDataType() {
                return Game.class;
            }
        };

        //Gets the map of upvotes and configures it to call the upvote listener whenever it is modified
        resourceManagerMap.get(game.getId()).retrieveSingleWithUpdates(String.format(Constants.GAME_PATH,
                game.getId()), upvoteListener);

    }

    /**
     * Sets the upvote icon to be either filled or empty depending on whether the user has upvoted
     * the game.
     *
     * @param upvoteIcon The view being affected
     * @param hasUpvoted Whether the user has upvoted the game
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
     * Sets the initial/default view for the upvote icon and upvote number, and sets the click
     * listener for the upvote icon.
     *
     * @param holder The holder for the view
     * @param game The game being affected
     */
    private void setUpvoteView(WallViewHolder holder, Game game, int numUpvotes,
                               boolean hasUpvoted) {
        // Set numberUpvotesText to be the the number of upvotes;
        holder.numberUpvotesText.setText(NumberFormat.getInstance().format(numUpvotes));
        // Sets the click listener, which changes implementation depending on upvote status
        holder.upvoteIcon.setOnClickListener(new WallViewAdapter.UpvoteClickListener(game, hasUpvoted));
        // Sets the icon depending on whether it has been upvoted
        setUpvoteIcon(holder.upvoteIcon, hasUpvoted);
    }

    private void displayCaption(WallViewHolder holder, Game game) {
        if (game.getTopCaption() != null) {
            holder.captionerText.setVisibility(TextView.VISIBLE);
            holder.captionText.setText(game.getTopCaption().retrieveCaptionText());
            displayUser(holder.captionerText, null, String.format(Constants.USER_PATH, game.getTopCaption().getUserId()));
        }
        else {
            // display a request to participate over the caption's view if a caption does not exist
            holder.captionText.setText(R.string.caption_filler);
            holder.captionerText.setVisibility(TextView.GONE);
        }
    }

    /**
     * Sets the caption text based on whether the game is public/private, closed/open.
     *
     * @param holder The viewholder containing the caption text
     * @param game The game object being referenced
     */
    private void setCaptionTextStyle(WallViewHolder holder, Game game) {
        if (!game.getIsPublic() && !game.getIsOpen()) {
            // If private and closed, bold italic
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.BOLD_ITALIC);
        } else if (game.getIsOpen() && game.getIsPublic()) {
            // If open and public, normal
            holder.captionText.setTypeface(Typeface.create(holder.captionText.getTypeface(),
                    Typeface.NORMAL), Typeface.NORMAL);
        } else if (!game.getIsOpen()){
            // If just closed, bold
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.BOLD);
        } else {
            // If private, italicize
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.ITALIC);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Displays the profile picture and username of a valid User. Shows the default if invalid
     * @param username the TextView from the holder, for either the Picker or Captioner's name
     * @param photo the ImageView from the holder, for either the Picker or Captioner's photo
     * @param userPath The path to the desired User
     */
    private void displayUser(final TextView username, final ImageView photo, String userPath) {
        // remove this portion if firebase is guaranteed to not have invalid users
        username.setText(" ");
        if (photo != null) {
            Glide.with(photo.getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(photo);
        }

        // ensure the user id is a valid one to avoid errors
        if (validFirebasePath(userPath)) {
            // display the name and profile picture if a valid user is obtained from the user id
            FirebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    // replace default is the User is valid
                    if (user != null) {
                        // if there is no photo path, don't display it, use a - instead
                        if (photo != null) {
                            username.setText(user.getDisplayName());
                            FirebaseResourceManager.loadImageIntoView(user.getImagePath(), photo);
                        }
                        else {
                            username.setText(activity.getResources().getString(R.string.captioner_name, user.getDisplayName()));
                        }

                    }
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    /**
     * Adds a list of games.
     *
     * @param newGames a list of games to add
     */
    public void addItems(List<Game> newGames) {
        int startPos = items.size();
        items.addAll(newGames);
        this.notifyItemRangeChanged(startPos, newGames.size());
    }

    /**
     * Called when an upvote is clicked. Adds/removes the upvote to/from firebase depending on
     * whether hasUpvoted is true or false.
     *
     * @param upvoteIcon The view being clicked
     * @param game The game object being affected
     * @param hasUpvoted Whether the user has previously upvoted this game
     */
    private void handleClickUpvote(final ImageView upvoteIcon, Game game,
                                   boolean hasUpvoted) {
        // Listens to see if anything went wrong
        Uploader.UploadListener listener = new Uploader.UploadListener() {
            @Override
            public void onComplete() {}

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(activity, activity.getResources().getString(R.string.upvote_error),
                        Toast.LENGTH_SHORT).show();
            }
        };

        if (game != null) {
            // Remove the upvote if the user has upvoted
            if (hasUpvoted) {
                FirebaseUploader.removeUpvote(
                        String.format(Constants.GAME_UPVOTE_PATH, game.getId(),
                                FirebaseResourceManager.getUserId()), listener);
            }
            // Add the upvote if the user hasn't upvoted
            else {
                FirebaseUploader.addUpvote(
                        String.format(Constants.GAME_UPVOTE_PATH, game.getId(),
                                FirebaseResourceManager.getUserId()), listener);
            }
        }
    }
}
