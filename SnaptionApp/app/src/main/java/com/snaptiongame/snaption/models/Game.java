package com.snaptiongame.snaption.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.DAYS;
import static com.snaptiongame.snaption.Constants.HOURS;
import static com.snaptiongame.snaption.Constants.HOURS_PER_DAY;
import static com.snaptiongame.snaption.Constants.MILLIS_PER_SECOND;
import static com.snaptiongame.snaption.Constants.MINUTES;
import static com.snaptiongame.snaption.Constants.MINUTES_PER_HOUR;
import static com.snaptiongame.snaption.Constants.REMAINING;
import static com.snaptiongame.snaption.Constants.SECONDS_PER_MINUTE;
import static com.snaptiongame.snaption.Constants.SINGULAR;

/**
 * Class to keep track of game info.
 *
 * @author Cameron Geehr
 */

public class Game implements Serializable {
    private GameData data; //The object containing the players and captions
    private GameMetadata metaData; //The object containing all other information about the game


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
    public Game(@NonNull GameData data, @NonNull GameMetadata metaData) {
        this.data = data;
        this.metaData = metaData;
    }

    /** Setter Methods **/

    /**
     * Sets the map of upvotes.
     *
     * @param upvotes The map of upvotes
     */
    public void setUpvotes(Map<String, Integer> upvotes) {
        metaData.setUpvotes(upvotes);
    }

    /**
     * Sets the game's top caption.
     * Currently unused.
     *
     * @param topCaption
     */
    public void setTopCaption(Caption topCaption) {
        metaData.setTopCaption(topCaption);
    }

    public void setCaptions(Map<String, Caption> captions) {
        data.setCaptions(captions);
    }

    public void setPlayers(Map<String, Integer> players) {
        data.setPlayers(players);
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
    public GameMetadata getMetaData() {
        return metaData;
    }

    /**
     * Returns the ID of the game.
     *
     * @return The ID of the game
     */
    public String getId() {
        return metaData.getId();
    }

    /**
     * Returns the ID of the player that started the game.
     *
     * @return The ID of the picker
     */
    public String getPickerId() {
        return metaData.getPickerId();
    }

    /**
     * Returns the path of the image on Firebase.
     *
     * @return The location of the game's image
     */
    public String getImagePath() {
        return metaData.getImagePath();
    }

    /**
     * Returns the caption list.
     *
     * @return The list of captions in the game
     */
    public Map<String, Caption> getCaptions() {
        return data.getCaptions();
    }

    /**
     * Returns the caption list.
     *
     * @return The list of captions in the game
     */
    public Map<String, Integer> getUpvotes() {
        return metaData.getUpvotes();
    }

    /**
     * Returns the list of players.
     *
     * @return The list of players
     */
    public Map<String, Integer> getPlayers() {
        return data.getPlayers();
    }

    /**
     * Returns the list of tags associated with the game.
     *
     * @return The list of tags
     */
    public Map<String, Integer> getTags() {
        return metaData.getTags();
    }

    /**
     * Returns whether the game is public.
     *
     * @return Whether the game is public
     */
    public boolean getIsPublic() {
        return metaData.getIsPublic();
    }

    /**
     * Returns whether the game is still ongoing.
     *
     * @return Whether the game is still going
     */
    public boolean isOpen() {
        return metaData.isOpen();
    }

    /**
     * Returns when the game ends.
     *
     * @return The time when the game ends
     */
    public long getEndDate() {
        return metaData.getEndDate();
    }

    /**
     * Returns when the game started.
     *
     * @return The time when the game was started
     */
    public long getCreationDate() {
        return metaData.getCreationDate();
    }

    /**
     * Returns the top caption. If the game is open, the top caption should be the caption with the
     * most upvotes, and upon a tie, most recent. If the game is closed, the top caption should be
     * the winning caption.
     *
     * @return The top caption
     */
    public Caption getTopCaption() {
        return metaData.getTopCaption();
    }

    /**
     * Returns the aspect ratio for the image.
     *
     * @return the aspect ratio for the image
     */
    public double getImageAspectRatio() {
        return metaData.getImageAspectRatio();
    }

    /**
     * Gets the remaining time compared with the inputted date and the endDate of this Game.
     * @param curTime the current date to compare with.
     * @return the remaining Days, Hours, or Minutes in a formatted String
     */
    public String remainingTime(long curTime) {
        // gets the difference between the dates and converts it to minutes
        long diff = (metaData.getEndDate() * MILLIS_PER_SECOND - curTime) / (SECONDS_PER_MINUTE * MILLIS_PER_SECOND);
        String result = diff % MINUTES_PER_HOUR + MINUTES;
        diff /= MINUTES_PER_HOUR;
        // use hours instead if it's longer than 60 minutes
        if (diff != 0) {
            result = diff % HOURS_PER_DAY + HOURS;
            diff /= HOURS_PER_DAY;
            // use days instead if it's longer than 24 hours
            if (diff != 0) {
                result = diff + DAYS;
            }
        }

        // show that it is plural if applicable
        if (!result.startsWith(SINGULAR)) {
            result += 's';
        }
        result += REMAINING;
        return result.toString();
    }
}
