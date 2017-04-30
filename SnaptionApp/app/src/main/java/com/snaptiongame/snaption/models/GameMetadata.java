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

public class GameMetadata implements Serializable {

    private String id; //The ID of the game
    private String pickerId; //The ID of the picker
    private String imagePath; //The path of the image on Firebase
    private Map<String, Integer> tags; //The map of tags of the image
    private Map<String, Integer> upvotes; //The map of players who have upvoted
    private Caption topCaption; //The top caption of the image
    private boolean isPublic; //Whether the game is viewable to to the public
    private long endDate; //When the game ends
    private long creationDate; //When the game was created
    private double imageAspectRatio; //The aspect ratio of the image

    /**
     * Default constructor for Firebase.
     */
    public GameMetadata() {}

    /**
     * Constructor with creationDate to test with dependency injection.
     *
     * @param id The ID of the game
     * @param pickerId The ID of the picker
     * @param imagePath The image path in firebase
     * @param tags The map of tags of the image
     * @param isPublic Whether the game is viewable to to the public
     * @param endDate When the game ends
     * @param creationDate When the game was created
     * @param imageAspectRatio The aspect ratio for the image
     */
    public GameMetadata(String id, String pickerId, String imagePath, Map<String, Integer> tags,
                        boolean isPublic, long endDate, long creationDate, double imageAspectRatio) {
        this.id = id;
        this.pickerId = pickerId;
        this.imagePath = imagePath;
        this.tags = tags;
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.imageAspectRatio = imageAspectRatio;
    }

    /**
     * Normal constructor with all fields but creationDate.
     *
     * @param id The ID of the game
     * @param pickerId The ID of the picker
     * @param imagePath The image path in firebase
     * @param tags The map of tags of the image
     * @param isPublic Whether the game is viewable to to the public
     * @param endDate When the game ends
     * @param imageAspectRatio The aspect ratio for the image
     */
    public GameMetadata(String id, String pickerId, String imagePath, Map<String, Integer> tags,
                        boolean isPublic, long endDate, double imageAspectRatio) {
        //Calls the other constructor with creationDate calculated
        this(id, pickerId, imagePath, tags, isPublic, endDate,
                Calendar.getInstance().getTimeInMillis() / MILLIS_PER_SECOND, imageAspectRatio);
    }

    public String getId() {
        return id;
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

    public double getImageAspectRatio() {
        return imageAspectRatio;
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
