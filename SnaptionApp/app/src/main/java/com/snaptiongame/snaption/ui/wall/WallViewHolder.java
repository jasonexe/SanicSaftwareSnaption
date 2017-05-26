package com.snaptiongame.snaption.ui.wall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TintContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.snaptiongame.snaption.ui.games.GameActivity;
import com.snaptiongame.snaption.ui.new_game.CreateGameActivity;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;
import com.snaptiongame.snaption.utilities.ViewUtilities;

import java.text.NumberFormat;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.snaptiongame.snaption.Constants.GAME_PRIVATE_METADATA_UPVOTE_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_METADATA_UPVOTE_PATH;
import static com.snaptiongame.snaption.R.id.photo;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewHolder extends RecyclerView.ViewHolder {
    private static final int UPVOTE_ANIM_DIST = 40;
    private static final int UPVOTE_ANIM_DURATION = 800;
    private static final float UPVOTE_ANIM_SCALE = 0.5f;

    @BindView(R.id.picker_name)
    public TextView pickerName;
    @BindView(photo)
    public AspectRatioImageView gamePhoto;
    @BindView(R.id.caption_text)
    public TextView captionText;
    @BindView(R.id.captioner_container)
    public LinearLayout captionerLayout;
    @BindView(R.id.captioner_name)
    public TextView captionerName;
    @BindView(R.id.captioner_photo)
    public ImageView captionerPhoto;
    @BindView(R.id.game_info)
    public RelativeLayout gameInfoLayout;
    @BindView(R.id.upvote_icon)
    public ImageView upvoteIcon;
    @BindView(R.id.upvote_count)
    public TextView upvoteCountText;

    private GameMetadata game;

    public static WallViewHolder newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_game_item,
                parent, false);
        return new WallViewHolder(view);
    }

    private WallViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Populates the view with information from the game metadata
     *
     * @param game The GameMetadata being referenced
     */
    public void populate(GameMetadata game) {
        this.game = game;

        // populate the game photo
        gamePhoto.setImageAspectRatio(game.getImageAspectRatio());
        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), gamePhoto);
        ViewCompat.setTransitionName(gamePhoto, game.getId());

        // populate the picker view
        displayPicker(game);

        // populate the top caption view
        displayCaption(game);
        setCaptionTextStyle(game);

        // populate the the upvotes view
        Map<String, Integer> upvoters = game.getUpvotes();
        if(upvoters != null) {
            setUpvoteView(upvoters.size(), upvoters.containsKey(FirebaseUserResourceManager
                    .getUserId()), false);
        } else {
            setUpvoteView(0, false, false);
        }
    }

    /**
     * Clears the view of information from the game metadata
     */
    public void clear() {
        this.game = null;
        pickerName.setText("");
        gamePhoto.setImageDrawable(null);
        captionText.setText("");
        captionerName.setText("");
        captionerPhoto.setImageDrawable(null);
        upvoteIcon.setImageDrawable(null);
        upvoteCountText.setText("");
    }

    @OnClick(photo)
    protected void onClickPhoto(View view) {
        // start game activity with shared image transition
        Context context = getActivityContext(view.getContext());
        if (context instanceof TintContextWrapper) {
            context = ((TintContextWrapper) context).getBaseContext();
        }

        Intent createGameIntent = new Intent(context, GameActivity.class);
        createGameIntent.putExtra(Constants.GAME, game);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((AppCompatActivity) context, view,
                        game.getId());
        context.startActivity(createGameIntent, options.toBundle());
    }

    @OnLongClick(photo)
    protected boolean onLongClickPhoto(View view) {
        final Context buttonContext = getActivityContext(view.getContext());
        //if the user is logged in
        if (FirebaseUserResourceManager.getUserId() != null) {
            // If they want to create a game from this one, start the create game intent
            // with this game's image path
            FirebaseResourceManager.getImageURI(game.getImagePath(), new ResourceListener<Uri>() {
                @Override
                public void onData(Uri data) {
                    Intent createGameIntent = new Intent(buttonContext, CreateGameActivity.class);
                    createGameIntent.putExtra(Constants.EXTRA_MESSAGE, data);
                    createGameIntent.putExtra(Constants.PHOTO_PATH, game.getImagePath());
                    createGameIntent.putExtra(Constants.ASPECT_RATIO, game.getImageAspectRatio());
                    buttonContext.startActivity(createGameIntent);
                }

                @Override
                public Class getDataType() {
                    return Uri.class;
                }
            });
        }
        else { //prompt user to log in
            ((MainSnaptionActivity) buttonContext).loginDialog.show();
        }
        return true;
    }

    @OnClick(R.id.upvote_icon)
    protected void onClickUpvote(View view) {
        Context context = getActivityContext(view.getContext());
        // Check if user is logged in before letting them upvote. If not logged in, display
        // login dialog.
        if (FirebaseUserResourceManager.getUserId() == null) {
            ((MainSnaptionActivity) context).loginDialog.show();
        }
        else {
            Map<String, Integer> upvoters = game.getUpvotes();
            boolean hasUpvoted = upvoters != null && upvoters.containsKey(
                    FirebaseUserResourceManager.getUserId());
            handleClickUpvote(context, game, hasUpvoted);
        }
    }

    @OnClick(R.id.captioner_photo)
    protected void onClickCaptioner(View view) {
        //call create user profile activity
        if (game.getTopCaption() != null) {
            ProfileActivity.ProfileActivityCreator profileActivityCreator = ProfileActivity
                    .getProfileActivityCreator(getActivityContext(view.getContext()));
            profileActivityCreator.create(game.getTopCaption().userId);
        }
    }

    private Context getActivityContext(Context context) {
        if (context instanceof TintContextWrapper) {
            context = ((TintContextWrapper) context).getBaseContext();
        }
        return context;
    }

    /**
     * Sets the caption text based on whether the game is public/private, closed/open.
     *
     * @param game The GameMetadata being referenced
     */
    private void setCaptionTextStyle(GameMetadata game) {
        if (!game.getIsPublic() && !game.isOpen()) {
            // If private and closed, bold italic
            captionText.setTypeface(captionText.getTypeface(), Typeface.BOLD_ITALIC);
        } else if (game.isOpen() && game.getIsPublic()) {
            // If open and public, normal
            captionText.setTypeface(Typeface.create(captionText.getTypeface(),
                    Typeface.NORMAL), Typeface.NORMAL);
        } else if (!game.isOpen()) {
            // If just closed, bold
            captionText.setTypeface(captionText.getTypeface(), Typeface.BOLD);
        } else {
            // If private, italicize
            captionText.setTypeface(captionText.getTypeface(), Typeface.ITALIC);
        }
    }

    /**
     * Sets the initial/default view for the upvote icon and upvote number
     *
     * @param numUpvotes number of game upvotes
     * @param hasUpvoted whether the user has upvoted the game
     * @param animate whether the upvote icon should show a "ghost" animation
     */
    void setUpvoteView(int numUpvotes, final boolean hasUpvoted, boolean animate) {
        // Set upvoteCountText to be the the number of upvotes;
        upvoteCountText.setText(NumberFormat.getInstance().format(numUpvotes));
        // Sets the icon depending on whether it has been upvoted
        setUpvoteIcon(hasUpvoted, animate);
    }

    /**
     * Sets the upvote icon to be either filled or empty depending on whether the user has upvoted
     * the game.
     *
     * @param hasUpvoted Whether the user has upvoted the game
     * @param animate Whether the upvote icon should show a "ghost" animation
     */
    private void setUpvoteIcon(boolean hasUpvoted, boolean animate) {
        upvoteIcon.setImageDrawable(hasUpvoted ?
                ContextCompat.getDrawable(upvoteIcon.getContext(), R.drawable.thumb_up) :
                ContextCompat.getDrawable(upvoteIcon.getContext(), R.drawable.thumb_up_outline));
        if (animate) {
            ViewUtilities.animateGhost(gameInfoLayout, upvoteIcon, UPVOTE_ANIM_DIST,
                    UPVOTE_ANIM_DURATION, UPVOTE_ANIM_SCALE);
        }
    }

    /**
     * Sets the top caption of the game using the captioner's username and photo. If the top caption
     * does not exist, displays a message prompting users to caption the game.
     *
     * @param game The GameMetadata being referenced
     */
    private void displayCaption(GameMetadata game) {
        if (game.getTopCaption() != null) {
            captionerLayout.setVisibility(TextView.VISIBLE);
            captionText.setText(game.getTopCaption().retrieveCaptionText());
            displayCaptioner(game);
        }
        else {
            // display a request to participate over the caption's view if a caption does not exist
            captionText.setText(R.string.caption_filler);
            captionerLayout.setVisibility(TextView.GONE);
        }
    }

    /**
     * Sets the username of the picker of the game. If the game metadata contains the picker's
     * username, that username will be displayed. If the game metadata does not contain the picker's
     * username, the picker's username will be retrieved, displayed, and updated in the game
     * metadata.
     *
     * @param game The GameMetadata being referenced
     */
    private void displayPicker(final GameMetadata game) {
        if (game.getPickerUsername() != null) {
            setUserInfo(pickerName, game.getPickerUsername(), null, null);
        }
        else {
            // ensure the user id is a valid one to avoid errors
            if (FirebaseUserResourceManager.isValidUser(game.getPickerId())) {
                // display the name and profile picture if a valid user is obtained from the user id
                FirebaseUserResourceManager.getUserMetadataById(game.getPickerId(),
                        new ResourceListener<UserMetadata>() {
                    @Override
                    public void onData(final UserMetadata user) {
                        if (user != null) {
                            setUserInfo(pickerName, user.getDisplayName(), null, null);
                            game.setPickerUsername(user.getDisplayName());

                        } else {
                            setDefaultUserInfo(pickerName, null);
                        }
                    }

                    @Override
                    public Class getDataType() {
                        return UserMetadata.class;
                    }
                });
            } else {
                setDefaultUserInfo(pickerName, null);
            }
        }
    }

    /**
     * Sets the username and photo of the top captioner of the game. If the top caption in the game
     * metadata contains the captioner's username and photo, they will be displayed. If the top
     * caption in the game metadata does not contain the captioner's username and photo, the
     * captioner's username and photo will be retrieved, displayed, and updated in the top caption
     * of the game metadata.
     *
     * @param game The GameMetadata being referenced
     */
    private void displayCaptioner(final GameMetadata game) {
        if (game.getTopCaption().userUsername != null) {
            setUserInfo(captionerName, game.getTopCaption().userUsername, captionerPhoto,
                    game.getTopCaption().userPhotoPath);
        }
        else {
            // ensure the user id is a valid one to avoid errors
            if (FirebaseUserResourceManager.isValidUser(game.getPickerId())) {
                // display the name and profile picture if a valid user is obtained from the user id
                FirebaseUserResourceManager.getUserMetadataById(game.getTopCaption().userId,
                        new ResourceListener<UserMetadata>() {
                            @Override
                            public void onData(final UserMetadata user) {
                                if (user != null) {
                                    setUserInfo(captionerName, user.getDisplayName(),
                                            captionerPhoto, user.getImagePath());
                                    game.getTopCaption().userUsername = user.getDisplayName();
                                    game.getTopCaption().userPhotoPath = user.getImagePath();

                                } else {
                                    setDefaultUserInfo(captionerName, captionerPhoto);
                                }
                            }

                            @Override
                            public Class getDataType() {
                                return UserMetadata.class;
                            }
                        });
            } else {
                setDefaultUserInfo(captionerName, captionerPhoto);
            }
        }
    }

    private void setDefaultUserInfo(TextView username, ImageView photo) {
        username.setText("");
        if (photo != null) {
            Glide.with(photo.getContext())
                    .load(R.drawable.com_facebook_profile_picture_blank_square).into(photo);
        }
    }

    private void setUserInfo(TextView usernameView, String username, ImageView photo,
                             String photoPath) {
        usernameView.setText(username);
        if (photo != null && photoPath != null) {
            FirebaseResourceManager.loadImageIntoView(photoPath, photo);
        }
    }

    /**
     * Called when an upvote is clicked. Adds/removes the upvote to/from firebase depending on
     * whether hasUpvoted is true or false.
     *
     * @param game The GameMetadata being referenced
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
}