package com.snaptiongame.snaptionapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to keep track of game info.
 * @author Cameron Geehr
 */

public class Game {
    private String id; //The ID of the game
    private String picker; //The ID of the picker
    private String imagePath; //The path of the image on Firebase
    private Map<String, Caption> captionsList; //The list of captions
    private List<String> playerList; //The list of players
    private List<String> categories; //The list of categories
    private boolean isPublic; //Whether the game is open to the public
    private boolean isOpen; //Whether the game is still open
    private int endDate; //When the game ends
    private int creationDate; //When the game was created
    private int judgerRating; //The rating that judgers have given the game
    private String maturityRating;
    private String winner;
    private String peoplesChoice;

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
    public Game(String id, String picker, String imagePath, List<String> playerList,
                List<String> categories, boolean isPublic, int endDate, int creationDate,
                String maturityRating) {
        this.id = id;
        this.picker = picker;
        this.imagePath = imagePath;
        this.playerList = new ArrayList<String>(playerList);
        this.categories = new ArrayList<String>(categories);
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.maturityRating = maturityRating;

        captionsList = new HashMap<String, Caption>();
        isOpen = true;
        judgerRating = 0;
    }

    /**
     * Adds a caption to the map.
     *
     * @param key The key associated with the caption
     * @param caption The caption being added to the game
     */
    public void addCaption(String key, Caption caption) {
        captionsList.put(key, caption);
    }

    /**
     * Adds a player to the game.
     *
     * @param playerId The ID of the player being added
     */
    public void addPlayer(String playerId) {
        playerList.add(playerId);
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
    public Map getCaptionsList() {
        return new HashMap(captionsList);
    }

    /**
     * Returns the list of players.
     *
     * @return The list of players
     */
    public List getPlayerList() {
        return new ArrayList(playerList);
    }

    /**
     * Returns the list of categories associated with the game.
     *
     * @return The list of categories
     */
    public List getCategories() {
        return new ArrayList(categories);
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
    public int getEndDate() {
        return endDate;
    }

    /**
     * Returns when the game started.
     *
     * @return The time when the game was started
     */
    public int getCreationDate() {
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

}
