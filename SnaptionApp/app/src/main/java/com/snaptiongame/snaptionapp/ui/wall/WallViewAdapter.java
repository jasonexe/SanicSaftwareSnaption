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

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.CreateGameActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.captions.CaptionActivity;

import java.util.List;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static com.snaptiongame.snaptionapp.servercalls.FirebaseUploader.imagePath;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {

    public static final String EXTRA_MESSAGE = "fromCurrentUri";
    public static final String PHOTO_PATH = "currentPhotoPath";
    public static final String USER_PATH = "users/";
    public static final int CLIP_TO_OUTLINE_MIN_SDK = 21;
    private final FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
    private List<Game> items;

    public WallViewAdapter(List<Game> items) {
        this.items = items;
    }

    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_game_item, parent, false);
        return new WallViewHolder(view);
    }

    class PhotoClickListener implements View.OnClickListener {
        String imagePath;
        public PhotoClickListener(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        public void onClick(View view) {
            Context imageContext = view.getContext();
            Intent createGameIntent = new Intent(imageContext, CaptionActivity.class);
            createGameIntent.putExtra(PHOTO_PATH, imagePath);
            imageContext.startActivity(createGameIntent);
        }
    }

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {
        Game game = items.get(position);
        holder.captionText.setText(game.getTopCaption() != null ?
                game.getTopCaption().retrieveCaptionText() :
                holder.captionerText.getContext().getResources().getString(R.string.caption_filler));
        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);
        holder.photo.setOnClickListener(new PhotoClickListener(game.getImagePath()));

        // distinguish between complete and incomplete games
        if (game.getIsOpen()) {
            holder.captionText.setTypeface(Typeface.create(holder.captionText.getTypeface(),
                    Typeface.NORMAL), Typeface.NORMAL);
        }
        else {
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.BOLD);
        }
        if (Build.VERSION.SDK_INT >= CLIP_TO_OUTLINE_MIN_SDK) {
            // allows the image to be clipped with rounded edges
            holder.photo.setClipToOutline(true);
        }

        final String imagePath = game.getImagePath();
        // TODO add a confirmation dialog that they want to create the new game??
        holder.createFromExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If they want to create a game from this one, start the create game intent
                // with this game's image path
                FirebaseResourceManager.getImageURI(imagePath, new ResourceListener<Uri>() {
                    @Override
                    public void onData(Uri data) {
                        Context buttonContext = holder.createFromExisting.getContext();
                        Intent createGameIntent = new Intent(buttonContext, CreateGameActivity.class);
                        createGameIntent.putExtra(EXTRA_MESSAGE, data);
                        createGameIntent.putExtra(PHOTO_PATH, imagePath);
                        buttonContext.startActivity(createGameIntent);
                    }

                    @Override
                    public Class getDataType() {
                        return Uri.class;
                    }
                });
            }
        });

        // TODO add the actual captioner name and photo instead of picker's
        // game.getTopCaption().getUserId() instead of game.getPicker()
        displayUser(holder, USER_PATH + game.getPicker());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Displays the profile picture and username of a valid User. Shows the default if invalid
     * @param holder The view that needs to be set with the User's name and avatar
     * @param userPath The path to the desired User
     */
    private void displayUser(final WallViewHolder holder, String userPath) {
        // remove this portion if firebase is guaranteed to not have invalid users
        holder.captionerText.setText(" ");
        Glide.with(holder.captionPhoto.getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(holder.captionPhoto);

        // ensure the user id is a valid one to avoid errors
        if(validFirebasePath(userPath)) {
            // display the name and profile picture if a valid user is obtained from the user id
            firebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    // replace default is the User is valid
                    if (user != null) {
                        holder.captionerText.setText(user.getDisplayName());
                        FirebaseResourceManager.loadImageIntoView(user.getImagePath(), holder.captionPhoto);
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
     * Checks if a String is valid for a Firebase path by making sure it does not contain
     * any of the following characters: '.', '#', '$', '[', or ']'
     *
     * @param path The path to be checked
     * @return True if the path does not contain any of the characters, false otherwise.
     */
    public static boolean validFirebasePath(String path) {
        Pattern pattern = Pattern.compile("[.#$\\[\\]]");
        return !pattern.matcher(path).find();
    }

    public void addItems(List<Game> newGames) {
        int startPos = items.size();
        items.addAll(newGames);
        this.notifyItemRangeChanged(startPos, newGames.size());
    }
}
