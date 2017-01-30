package com.snaptiongame.snaptionapp.servercalls;

import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.User;

/**
 * This is a generic interface to provide structure for our backend solution
 * The client is only allowed to upload information to our backend with these provided methods
 * Created by austinrobarts on 1/21/17.
 */

public interface Uploader {
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
    public void addUser(User user, byte[] photo);

    /**
     * Responsbile for:
     *  adds to a map in users table, increment upvote in the caption in game and user table
     * @param captionId
     * @param upvoterId
     * @param captionerId
     * @param gameId
     */
    public void addUpvote(String captionId, String upvoterId, String captionerId, String gameId);

    /**
     * Create a new key from backend to use as an Upvote's identity
     * @return a string representing a key so that it can be found later
     */
    public String getNewUpvoteKey();
}
