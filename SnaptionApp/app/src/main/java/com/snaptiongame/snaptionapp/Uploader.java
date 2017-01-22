package com.snaptiongame.snaptionapp;

import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.User;

/**
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
    public void addGame(Game game, byte[] photo);

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
     * Responsible for:
     *  upload profile picture
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
}
