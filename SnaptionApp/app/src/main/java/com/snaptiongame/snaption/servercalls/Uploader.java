package com.snaptiongame.snaption.servercalls;

import android.graphics.Bitmap;
import android.net.Uri;

import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;

/**
 * This is a generic interface to provide structure for our backend solution
 * The client is only allowed to upload information to our backend with these provided methods
 * Created by austinrobarts on 1/21/17.
 */

public interface Uploader {
    public String ITEM_ALREADY_EXISTS_ERROR = "Item already exists";
    public interface UploadListener{
        public void onComplete();
        /**
         * Called when the Uploader is unable to finish a transaction
         * @param errorMessage an empty string or one of the predefined Uploader errors
         */
        public void onError(String errorMessage);
    }

    /**
     * Responsible for:
     *  notify invited users
     *  upload photo to storage
     *  add gameId to user's GameList
     *  add Game object to Games table
     *  does not populate any fields of Game object
     * @param game
     * @param photo
     */
    public void addGame(Game game, byte[] data, double aspectRatio,
                        FirebaseUploader.UploadDialogInterface uploadCallback);

    /**
     * Responsible for uploading a game whose photo is already in storage.
     * @param game Game object with fields filled in
     */
    public void addGame(Game game);

    /**
     * Create a new key from backend to use as a Game's identity
     *
     * @param isPublic What accessibility the game has
     * @return a string representing a key so that it can be found later
     */
    public String getNewGameKey(boolean isPublic);

    /**
     * Responsible for:
     *  notifications still mystery
     *  adds caption to the game in firebase
     *  adds caption to the user captions list
     *
     * @param caption
     * @param isPublic
     */
    public void addCaptions(Caption caption, boolean isPublic);

    /**
     * Create a new key from backend to use as a Caption's identity
     * @param game The game the caption will be in.
     * @return a string representing a key so that it can be found later
     */
    public String getNewCaptionKey(Game game);
    /**
     * Responsible for:
     *  uploading profile picture
     *
     * @param user
     * @param photo
     */
    public void addUser(UserMetadata user, byte[] photo, ResourceListener<UserMetadata> listener);

    /**
     * Responsible for adding the friend to the user's friends list and adding the user to the
     * friend's friends list
     *
     * @param user
     * @param friend
     */
    public void addFriend(UserMetadata user, Friend friend, UploadListener listener);
}
