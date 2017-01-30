package com.snaptiongame.snaptionapp.servercalls;

import com.snaptiongame.snaptionapp.models.Game;

/**
 * Created by brittanyberlanga on 1/24/17.
 */

public interface GameResourceManager {
    void retrieveGamesByCreationDate();
    /**
     * This method finds the game object by given ID in the backend and returns it to the given
     * listener.
     * @param gameId the id of the game to be gotten
     * @param gameListener the listener to receive the game once it is found
     */
    public void retrieveGameById(String gameId, final ResourceListener<Game> gameListener);
}
