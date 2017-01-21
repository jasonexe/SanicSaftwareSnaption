package com.snaptiongame.snaptionapp;

import com.snaptiongame.snaptionapp.models.Game;

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
     * @param gameId
     * @param userId
     */
    public void addCaptions(String gameId, String userId);

    /**
     * Responsible for:
     *  upload profile picture
     *
     * @param userId
     */
    public void addUser(String userId);

    /**
     * Responsbile for:
     *  adds to a map in users table, increment upvote in the caption in game and user table
     * @param captionId
     * @param userId
     * @param gameId
     */
    public void addUpvote(String captionId, String userId, String gameId);
}
