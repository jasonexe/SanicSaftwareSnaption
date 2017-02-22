package com.snaptiongame.snaptionapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int judgerRating; //The rating that judgers have given the game
    private String maturityRating; //The maturity rating of the game
    private List<String> categories; //The list of categories
    private String winner; //The id of the winning caption
    private String peoplesChoice; //The id of the caption selected by the players

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
     * @param playerList The list of players in the game
     * @param categories The list of categories that the game belongs to
     * @param isPublic Whether the game is available to the public
     * @param endDate The time when the game ends
     * @param creationDate The time when the game was started
     * @param maturityRating The maturity rating of the card
     */
    public Game(String id, String picker, String imagePath, Map<String, Integer> playerList,
                List<String> categories, boolean isPublic, long endDate, long creationDate,
                String maturityRating) {
        this.id = id;
        this.picker = picker;
        this.imagePath = imagePath;
        this.players = new HashMap<>(playerList);
        this.categories = new ArrayList<>(categories);
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.maturityRating = maturityRating;

        captions = new HashMap<>();
        isOpen = true;
        judgerRating = 0;
        winner = "";
        peoplesChoice = "";
    }

    /**
     * Constructs a game.
     *
     * @param id The unique ID of the game
     * @param picker The player ID of the picker
     * @param imagePath The path of the image on Firebase
     * @param playerList The list of players in the game
     * @param categories The list of categories that the game belongs to
     * @param isPublic Whether the game is available to the public
     * @param endDate The time when the game ends
     * @param maturityRating The maturity rating of the card
     */
    public Game(String id, String picker, String imagePath, Map<String, Integer> playerList,
                List<String> categories, boolean isPublic, long endDate, String maturityRating) {
        this.id = id;
        this.picker = picker;
        this.imagePath = imagePath;
        this.players = new HashMap<>(playerList);
        this.categories = new ArrayList<>(categories);
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.maturityRating = maturityRating;

        captions = new HashMap<>();
        isOpen = true;
        judgerRating = 0;
        creationDate = Calendar.getInstance().getTimeInMillis();
        winner = "";
        peoplesChoice = "";
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
     * Sets the winner of the people's choice.
     *
     * @param captionId The caption that the players selected as a winner
     */
    public void setPeoplesChoice(String captionId) {
        peoplesChoice = captionId;
    }

    /**
     * Sets the game as over.
     */
    public void closeGame() {
        isOpen = false;
    }

    /**
     * Upvotes the game.
     */
    public void upvote() {
        judgerRating++;
    }

    /**
     * Downvotes the game.
     */
    public void downvote() {
        if (judgerRating > 0)
            judgerRating--;
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
    public Map getCaptions() {
        if (captions == null) {
            return null;
        }
        return new HashMap(captions);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Returns the list of players.
     *
     * @return The list of players
     */
    public Map getPlayers() {
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
    public List getCategories() {
        if (categories == null) {
            return null;
        }
        return new ArrayList<>(categories);
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
        return isOpen;
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
     * Returns the rating that judgers have given the game.
     *
     * @return The rating that judgers have given the game
     */
    public int getJudgerRating() {
        return judgerRating;
    }

    /**
     * Returns the maturity rating for the game.
     *
     * @return The maturity rating for the game
     */
    public String getMaturityRating() {
        return maturityRating;
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
     * Returns the ID of the caption that stickers have given the most votes to.
     *
     * @return The ID of the people's choice caption
     */
    public String getPeoplesChoice() {
        return peoplesChoice;
    }

    /**
     * Returns the top caption. If the game is open, the top caption should be the caption with the
     * most votes. If the game is closed, the top caption should be the winning caption.
     *
     * @return The top caption.
     */
    public Caption getTopCaption() {
        // If no captions are made, return null
        if (captions == null) {
            return null;
        }
        if (getIsOpen()) {
            Collection<Caption> allCaptions = captions.values();
            int highestUpvoteNum = 0;
            Caption toReturn = null;
            // Find caption with the highest number of votes
            for (Caption caption : allCaptions) {
                if (caption.retrieveNumVotes() > highestUpvoteNum) {
                    highestUpvoteNum = caption.getVotes().size();
                    toReturn = caption;
                }
            }
            return toReturn;
        } else {
            // Otherwise, return what's in winner
            return captions.get(getWinner());
        }
    }
}
