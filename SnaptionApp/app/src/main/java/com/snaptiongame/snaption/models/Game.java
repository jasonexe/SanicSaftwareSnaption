package com.snaptiongame.snaption.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.MILLIS_PER_SECOND;

/**
 * Class to keep track of game info.
 *
 * @author Cameron Geehr
 */

public class Game implements Serializable {
    private GameData data; //The object containing the players and captions
    private GameMetaData metaData; //The object containing all other information about the game

    /**
     * Default constructor.
     */
    public Game() {}

    /**
     * Creates the game using data and metadata.
     *
     * @param data The object containing the players and captions
     * @param metaData The object containing all other information about the game
     */
    public Game(@NonNull GameData data, @NonNull GameMetaData metaData) {
        this.data = data;
        this.metaData = metaData;
    }

    /** Setter Methods **/

    /**
     * Sets the game's data.
     *
     * @param data The object containing the players and captions
     */
    public void setData(GameData data) {
        this.data = data;
    }

    /**
     * Sets the game's metadata.
     *
     * @param metaData The object containing all other information about the game
     */
    public void setMetaData(GameMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * Sets the map of upvotes.
     *
     * @param upvotes The map of upvotes
     */
    public void setUpvotes(Map<String, Integer> upvotes) {
        if (metaData != null) {
            metaData.setUpvotes(upvotes);
        }
    }

    /**
     * Sets the game's top caption.
     *
     * @param topCaption
     */
    public void setTopCaption(Caption topCaption) {
        if (metaData != null) {
            metaData.setTopCaption(topCaption);
        }
    }

    public void setImagePath(String imagePath) {
        if (metaData != null) {
            metaData.setImagePath(imagePath);
        }
    }

    /** Accessor Methods **/

    /**
     * Returns the game's data.
     *
     * @return The object containing the players and captions
     */
    public GameData getData() {
        return data;
    }

    /**
     * Returns the game's metadata.
     *
     * @return The object containing all other information about the game
     */
    public GameMetaData getMetaData() {
        if (metaData == null) {
            return null;
        }
        return metaData;
    }

    /**
     * Returns the ID of the game.
     *
     * @return The ID of the game
     */
    public String getId() {
        if (metaData == null) {
            return null;
        }
        return metaData.getGameId();
    }

    /**
     * Returns the ID of the player that started the game.
     *
     * @return The ID of the picker
     */
    public String getPickerId() {
        if (metaData == null) {
            return null;
        }
        return metaData.getPickerId();
    }

    /**
     * Returns the path of the image on Firebase.
     *
     * @return The location of the game's image
     */
    public String getImagePath() {
        if (metaData == null) {
            return null;
        }
        return metaData.getImagePath();
    }

    /**
     * Returns the caption list.
     *
     * @return The list of captions in the game
     */
    public Map<String, Caption> getCaptions() {
        if (data == null) {
            return null;
        }
        return data.getCaptions();
    }

    /**
     * Returns the caption list.
     *
     * @return The list of captions in the game
     */
    public Map<String, Integer> getUpvotes() {
        if (metaData == null) {
            return null;
        }
        return metaData.getUpvotes();
    }

    /**
     * Returns the list of players.
     *
     * @return The list of players
     */
    public Map<String, Integer> getPlayers() {
        if (data == null) {
            return null;
        }
        return data.getPlayers();
    }

    /**
     * Returns the list of tags associated with the game.
     *
     * @return The list of tags
     */
    public Map<String, Integer> getTags() {
        if (metaData == null) {
            return null;
        }
        return metaData.getTags();
    }

    /**
     * Returns whether the game is public.
     *
     * @return Whether the game is public
     */
    public boolean getIsPublic() {
        if (metaData == null) {
            return false;
        }
        return metaData.getIsPublic();
    }

    /**
     * Returns whether the game is still ongoing.
     *
     * @return Whether the game is still going
     */
    public boolean getIsOpen() {
        if (metaData == null) {
            return false;
        }
        return (Calendar.getInstance().getTimeInMillis()/MILLIS_PER_SECOND) < metaData.getEndDate();
    }

    /**
     * Returns when the game ends.
     *
     * @return The time when the game ends
     */
    public long getEndDate() {
        if (metaData == null) {
            return 0;
        }
        return metaData.getEndDate();
    }

    /**
     * Returns when the game started.
     *
     * @return The time when the game was started
     */
    public long getCreationDate() {
        if (metaData == null) {
            return 0;
        }
        return metaData.getCreationDate();
    }

    /**
     * Returns the top caption. If the game is open, the top caption should be the caption with the
     * most upvotes. If the game is closed, the top caption should be the winning caption.
     * Will set the winner value if it is not already set and the game is closed.
     *
     * @return The top caption
     */
    public Caption getTopCaption() {
        Caption topCaption = null;
        Map<String, Caption> captions;
        if (data != null) {
            captions = data.getCaptions();

            //If the game hasn't ended, calculate the top caption
            if (getIsOpen()) {
                if (captions != null && captions.size() > 0) {
                    topCaption = Collections.min(captions.values());
                }
            }
            //If the game has ended
            else {
                if (metaData != null) {
                    topCaption = metaData.getTopCaption();
                }
                //Check to see if the top caption has been calculated already
                if (topCaption == null && captions != null && captions.size() > 0) {
                    //If it hasn't calculate it and save the result
                    topCaption = Collections.min(captions.values());
                    metaData.setTopCaption(topCaption);
                }
                //If it has, use the already calculated top caption
            }
        }

        return topCaption;
    }
}
