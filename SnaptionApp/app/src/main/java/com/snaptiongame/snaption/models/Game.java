package com.snaptiongame.snaption.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.MILLIS_PER_SECOND;

/**
 * Class to keep track of game info.
 *
 * @author Cameron Geehr
 */

public class Game implements Serializable {
    private String id; //The ID of the game
    private String picker; //The ID of the picker
    private boolean isPublic; //Whether the game is open to the public
    private Map<String, Caption> captions; //The list of captions
    private String imagePath; //The path of the image on Firebase
    private boolean isOpen; //Whether the game is still open
    private Map<String, Integer> players; //The list of players in the private game
    private long endDate; //When the game ends
    private long creationDate; //When the game was created
    private Map<String, Integer> categories; //The list of categories
    private String winner; //The id of the winning caption
    private Map<String, Integer> votes; //The list of votes

    /**
     * Default constructor.
     */
    public Game() {}

    /**
     * Constructs a game.
     *
     * @param id The unique ID of the game
     * @param picker The player ID of the picker
     * @param imagePath The path of the image on Firebase
     * @param players The list of players in the game
     * @param categories The list of categories that the game belongs to
     * @param isPublic Whether the game is available to the public
     * @param endDate The time when the game ends
     * @param creationDate The time when the game was started
     */
    public Game(String id, String picker, String imagePath, Map<String, Integer> players,
                Map<String, Integer> categories, boolean isPublic, long endDate,
                long creationDate) {
        this.id = id;
        this.picker = picker;
        this.imagePath = imagePath;
        this.players = new HashMap<>(players);
        this.categories = new HashMap<>(categories);
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.creationDate = creationDate;

        captions = new HashMap<>();
        votes = new HashMap<>();
        isOpen = true;
        winner = "";
    }

    /**
     * Constructs a game.
     *
     * @param id The unique ID of the game
     * @param picker The player ID of the picker
     * @param imagePath The path of the image on Firebase
     * @param players The list of players in the game
     * @param categories The list of categories that the game belongs to
     * @param isPublic Whether the game is available to the public
     * @param endDate The time when the game ends
     */
    public Game(String id, String picker, String imagePath, Map<String, Integer> players,
                Map<String, Integer> categories, boolean isPublic, long endDate) {
        this.id = id;
        this.picker = picker;
        this.imagePath = imagePath;
        this.players = new HashMap<>(players);
        this.categories = new HashMap<>(categories);
        this.isPublic = isPublic;
        this.endDate = endDate;

        captions = new HashMap<>();
        votes = new HashMap<>();
        isOpen = true;
        creationDate = Calendar.getInstance().getTimeInMillis() / MILLIS_PER_SECOND;
        winner = "";
    }

    /**
     * Adds a caption to the map.
     *
     * @param key The key associated with the caption
     * @param caption The caption being added to the game
     */
    public void addCaption(String key, Caption caption) {
        captions.put(key, caption);
    }

    /**
     * Adds a player to the game.
     *
     * @param playerId The ID of the player being added
     */
    public void addPlayer(String playerId) {
        players.put(playerId, 1);
    }

    /**
     * Sets the winner of the game.
     *
     * @param captionId The caption that won the game
     */
    public void setWinner(String captionId) {
        winner = captionId;
    }

    /**
     * Sets the game as over.
     */
    public void closeGame() {
        isOpen = false;
    }

    /** Accessor Methods **/

    /**
     * Returns the ID of the game.
     *
     * @return The ID of the game
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the ID of the player that started the game.
     *
     * @return The ID of the picker
     */
    public String getPicker() {
        return picker;
    }

    /**
     * Returns the path of the image on Firebase.
     *
     * @return The location of the game's image
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Returns the caption list.
     *
     * @return The list of captions in the game
     */
    public Map<String, Caption> getCaptions() {
        if (captions == null) {
            return null;
        }
        return new HashMap<>(captions);
    }

    /**
     * Returns the caption list.
     *
     * @return The list of captions in the game
     */
    public Map<String, Integer> getVotes() {
        if (votes == null) {
            return null;
        }
        return new HashMap<>(votes);
    }

    public void setVotes(Map<String, Integer> votes) {
        this.votes = votes;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Returns the list of players.
     *
     * @return The list of players
     */
    public Map<String, Integer> getPlayers() {
        if (players == null) {
            return null;
        }
        return new HashMap<>(players);
    }

    /**
     * Returns the list of categories associated with the game.
     *
     * @return The list of categories
     */
    public Map<String, Integer> getCategories() {
        if (categories == null) {
            return null;
        }
        return new HashMap<>(categories);
    }

    /**
     * Returns whether the game is public.
     *
     * @return Whether the game is public
     */
    public boolean getIsPublic() {
        return isPublic;
    }

    /**
     * Returns whether the game is still ongoing.
     *
     * @return Whether the game is still going
     */
    public boolean getIsOpen() {
        return isOpen || (Calendar.getInstance().getTimeInMillis()/MILLIS_PER_SECOND) > getEndDate();
    }

    /**
     * Returns when the game ends.
     *
     * @return The time when the game ends
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * Returns when the game started.
     *
     * @return The time when the game was started
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Returns the ID of the winning caption.
     *
     * @return The ID of the winning caption
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Returns the top caption. If the game is open, the top caption should be the caption with the
     * most votes. If the game is closed, the top caption should be the winning caption.
     * Will set the winner value if it is not already set and the game is closed.
     *
     * @return The top caption
     */
    public Caption getTopCaption() {
        Caption topCaption = null;
        // If no captions are made, return null
        if (captions != null && captions.size() > 0) {
            // Checks whether the game is over and whether a winner is set
            if (Calendar.getInstance().getTimeInMillis() < endDate
                    || winner == null || winner.length() == 0) {
                // Gets the min because captions are in reverse order
                topCaption = Collections.min(captions.values());
            }
            else {
                // Otherwise, return what's in winner
                topCaption = captions.get(winner);
            }
        }
        //Sets the winner if it hasn't been already
        if (Calendar.getInstance().getTimeInMillis() > endDate
                && (winner == null || winner.length() == 0)
                && topCaption != null) {
            winner = topCaption.getId();
        }
        return topCaption;
    }
}
