package com.snaptiongame.snaption.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.MILLIS_PER_SECOND;

/**
 * Class containing all basic game information, and fits Firebase data structure.
 *
 * @author Cameron Geehr
 */

public class GameMetaData implements Serializable {

    private String gameId; //The ID of the game
    private String pickerId; //The ID of the picker
    private String imagePath; //The path of the image on Firebase
    private Map<String, Integer> tags; //The map of tags of the image
    private Map<String, Integer> upvotes; //The map of players who have upvoted
    private Caption topCaption; //The top caption of the image
    private boolean isPublic; //Whether the game is viewable to to the public
    private long endDate; //When the game ends
    private long creationDate; //When the game was created

    /**
     * Default constructor for Firebase.
     */
    public GameMetaData() {}

    /**
     * Constructor with creationDate to test with dependency injection.
     *
     * @param gameId The ID of the game
     * @param pickerId The ID of the picker
     * @param imagePath The image path in firebase
     * @param tags The map of tags of the image
     * @param isPublic Whether the game is viewable to to the public
     * @param endDate When the game ends
     * @param creationDate When the game was created
     */
    public GameMetaData(String gameId, String pickerId, String imagePath, Map<String, Integer> tags,
                        boolean isPublic, long endDate, long creationDate) {
        this.gameId = gameId;
        this.pickerId = pickerId;
        this.imagePath = imagePath;
        this.tags = tags;
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.creationDate = creationDate;
    }

    /**
     * Normal constructor with all fields but creationDate.
     *
     * @param gameId The ID of the game
     * @param pickerId The ID of the picker
     * @param imagePath The image path in firebase
     * @param tags The map of tags of the image
     * @param isPublic Whether the game is viewable to to the public
     * @param endDate When the game ends
     */
    public GameMetaData(String gameId, String pickerId, String imagePath, Map<String, Integer> tags,
                        boolean isPublic, long endDate) {
        //Calls the other constructor with creationDate calculated
        this(gameId, pickerId, imagePath, tags, isPublic, endDate,
                Calendar.getInstance().getTimeInMillis() / MILLIS_PER_SECOND);
    }

    public String getId() {
        return gameId;
    }

    public String getPickerId() {
        return pickerId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Map<String, Integer> getTags() {
        if (tags == null) {
            return null;
        }
        return new HashMap<>(tags);
    }

    public Map<String, Integer> getUpvotes() {
        if (upvotes == null) {
            return null;
        }
        return new HashMap<>(upvotes);
    }

    public Caption getTopCaption() {
        return topCaption;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public long getEndDate() {
        return endDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public boolean isOpen() {
        return (Calendar.getInstance().getTimeInMillis() / MILLIS_PER_SECOND) < endDate;
    }

    public void setUpvotes(Map<String, Integer> upvotes) {
        this.upvotes = upvotes;
    }

    public void setTopCaption(Caption topCaption) {
        this.topCaption = topCaption;
    }
}
