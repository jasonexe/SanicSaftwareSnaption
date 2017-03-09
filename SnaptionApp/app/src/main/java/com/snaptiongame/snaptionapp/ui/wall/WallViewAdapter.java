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

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.ui.new_game.CreateGameActivity;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;

import java.util.List;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.validFirebasePath;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {

    private List<Game> items;
    private MainSnaptionActivity activity;

    public WallViewAdapter(List<Game> items, MainSnaptionActivity activity) {
        this.items = items;
        this.activity = activity;
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

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {
        Game game = items.get(position);
        // display the Picker of the game, the one who created it
        displayUser(holder.pickerName, holder.pickerPhoto, String.format(Constants.USER_PATH, game.getPicker()));

        // ensure the game has a top caption before displaying the caption and the captioner
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

        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);
        holder.photo.setOnClickListener(new PhotoClickListener(game));

        // distinguish between complete and incomplete games, and public/private
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
        if(validFirebasePath(userPath)) {
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

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }
}
