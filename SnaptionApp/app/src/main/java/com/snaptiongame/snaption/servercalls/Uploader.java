package com.snaptiongame.snaption.servercalls;

import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.User;

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
    public void addGame(Game game, byte[] photo,
                        FirebaseUploader.UploadDialogInterface uploadCallback);

    /**
     * Responsible for uploading a game whose photo is already in storage.
     * @param game Game object with fields filled in
     */
    public void addGame(Game game);

    /**
     * Create a new key from backend to use as a Game's identity
     * @return a string representing a key so that it can be found later
     */
    public String getNewGameKey();

    /**
     * Responsible for:
     *  notifications still mystery
     *  adds caption to the game in firebase
     *  adds caption to the user captions list
     *
     * @param caption
     */
    public void addCaptions(Caption caption);

    /**
     * Create a new key from backend to use as a Caption's identity
     * @param gameId The Id of the game that the caption will be in.
     * @return a string representing a key so that it can be found later
     */
    public String getNewCaptionKey(String gameId);
    /**
     * Responsible for:
     *  uploading profile picture
     *
     * @param user
     * @param photo
     */
    public void addUser(User user, byte[] photo, ResourceListener<User> listener);

    /**
     * Responsible for adding the friend to the user's friends list and adding the user to the
     * friend's friends list
     *
     * @param user
     * @param friend
     */
    public void addFriend(User user, Friend friend, UploadListener listener);
}
