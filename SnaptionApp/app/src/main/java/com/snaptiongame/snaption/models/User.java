package com.snaptiongame.snaption.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A model class for the User object.
 *
 * @author Austin Robarts
 */
public class User implements Person, Comparable<User>, Serializable {

    private UserMetadata metadata;
    private UserPublicData publicData;
    private UserPrivateData privateData;

    public User(UserMetadata metadata, UserPublicData publicData, UserPrivateData privateData) {
        this.metadata = new UserMetadata(metadata);
        this.publicData = new UserPublicData(publicData);
        this.privateData = new UserPrivateData(privateData);
    }

    public User(String id, String email, String displayName, String notificationId, String facebookId, String imagePath) {
        metadata = new UserMetadata(displayName, email, imagePath, notificationId, facebookId, id);
        publicData = new UserPublicData(null, null, null);
        privateData = new UserPrivateData(null, null, null);
    }

    public User(String id, String email, String displayName, String notificationId,
                String facebookId, String imagePath, Map<String, Integer> friends,
                Map<String, Integer> games, Map<String, Caption> captions,
                Map<String, String> joinedGames) {
        this(id, email, displayName, notificationId, facebookId, imagePath);
        publicData = new UserPublicData(captions, games, friends);
        privateData = new UserPrivateData(captions, games, joinedGames);
    }

    public Map<String, Integer> getFriends() {
            return publicData.getFriends();
    }

    public Map<String, String> getJoinedGames() {
        return privateData.getJoinedGames();
    }

    public Map<String, Integer> getCreatedPublicGames() {
        return publicData.getCreatedGames();
    }

    public Map<String, Integer> getCreatedPrivateGames() {
        return privateData.getCreatedGames();
    }

    public Map<String, Caption> getPublicCaptions() {
        return publicData.getCaptions();
    }

    public Map<String, Caption> getPrivateCaptions() {
        return privateData.getCaptions();
    }

    public List<Caption> getAllCaptions() {
        Map pubCaptionMap = getPublicCaptions();
        Map privateCaptionMap = getPrivateCaptions();
        List<Caption> captions = new ArrayList<>();
        if (pubCaptionMap != null) {
            captions.addAll(pubCaptionMap.values());
        }
        if (privateCaptionMap != null) {
            captions.addAll(privateCaptionMap.values());
        }
        return captions;
    }

    public List<Caption> getAllPublicCaptions() {
        Map pubCaptionMap = getPublicCaptions();
        List<Caption> captions = new ArrayList<>();
        if (pubCaptionMap != null) {
            captions.addAll(pubCaptionMap.values());
        }
        return captions;
    }

    public String getDisplayName() {
        return metadata.getDisplayName();
    }

    public String getLowercaseDisplayName() {
        String lowerName = null;
        String displayName = metadata.getDisplayName();
        if (displayName != null) {
            lowerName = displayName.toLowerCase();
        }
        return lowerName;
    }

    public String getEmail() {
        return metadata.getEmail();
    }

    public String getFacebookId() {
        return metadata.getFacebookId();
    }

    public String getId() {
        return metadata.getId();
    }

    public String getNotificationId() {
        return metadata.getNotificationId();
    }

    public String getImagePath() {
        return metadata.getImagePath();
    }

    public boolean getIsAndroid() {
        return metadata.getIsAndroid();
    }

    public void setDisplayName(String displayName) {
        metadata.setDisplayName(displayName);
    }

    public int getTotalCaptionCount() {
        int captionCount = 0;
        //get public and private
        Map pubCaptions = publicData.getCaptions();
        Map privateCaptions = privateData.getCaptions();
        if (pubCaptions != null) {
            captionCount += pubCaptions.size();
        }
        if (privateCaptions != null) {
            captionCount += privateCaptions.size();
        }
        return captionCount;
    }

    public int getTotalCreatedGamesCount() {
        int gameCount = 0;
        //get public and private games
        Map publicCreatedGames = publicData.getCreatedGames();
        Map privateCreatedGames = privateData.getCreatedGames();
        if (publicCreatedGames != null) {
            gameCount += publicCreatedGames.size();
        }
        if (privateCreatedGames != null) {
            gameCount += privateCreatedGames.size();
        }
        return gameCount;
    }

    public int getFriendCount() {
        int friendCount = 0;
        Map friends = publicData.getFriends();
        if(friends != null) {
            friendCount = friends.size();
        }
        return friendCount;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == getClass() && ((User) other).getId().equals(metadata.getId());
    }

    @Override
    public int compareTo(User other) {
        int result = getLowercaseDisplayName().compareTo(other.getLowercaseDisplayName());
        if (result == 0) {
            result = getEmail().compareTo(other.getEmail());
        }
        return result;
    }
}

