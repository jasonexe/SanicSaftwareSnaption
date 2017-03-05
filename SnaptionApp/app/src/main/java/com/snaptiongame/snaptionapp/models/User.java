package com.snaptiongame.snaptionapp.models;

import java.util.Map;

/**
 * Created by austinrobarts on 1/21/17.
 */

public class User implements Person {

    private String id;
    private String email;
    private String displayName;
    private Map<String, Integer> friends;
    private Map<String, Integer> createdGames;
    private Map<String, Caption> captions;
    private String notificationId;
    private String facebookId;
    private String imagePath;
    private Map<String, Integer> blockedUsers; // Ids of users this user has blocked
    private Map<String, Integer> privateGames; // Private games this user was invited to

    //needed for firebase compatibility
    public User() {}

    public User(String id, String email, String displayName, String notificationId, String facebookId, String imagePath) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.notificationId = notificationId;
        this.facebookId = facebookId;
        this.imagePath = imagePath;

        friends = null;
        createdGames = null;
        captions = null;
        blockedUsers = null;
        privateGames = null;
    }

    public User(String id, String email, String displayName, String notificationId,
                String facebookId, String imagePath, Map<String, Integer> friends,
                Map<String, Integer> games, Map<String, Caption> captions,
                Map<String, Integer> blockedUsers, Map<String, Integer> privateGames) {
        this(id, email, displayName, notificationId, facebookId, imagePath);
        this.friends = friends;
        this.createdGames = games;
        this.captions = captions;
        this.blockedUsers = blockedUsers;
        this.privateGames = privateGames;
    }

    public Map<String, Integer> getFriends() {
        return friends;
    }

    public Map<String, Integer> getBlockedUsers() {
        return blockedUsers;
    }

    public Map<String, Integer> getPrivateGames() {
        return privateGames;
    }

    public Map<String, Integer> getCreatedGames() {
        return createdGames;
    }

    public Map<String, Caption> getCaptions() {
        return captions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLowercaseDisplayName() {
        String lowerName = null;
        if (displayName != null) {
            lowerName = displayName.toLowerCase();
        }
        return lowerName;
    }

    public String getEmail() {
        return email;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getId() {
        return id;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setFriends(Map<String, Integer> friends) {
        this.friends = friends;
    }

    public void setCreatedGames(Map<String, Integer> games) {
        this.createdGames = games;
    }

    public void setCaptions(Map<String, Caption> captions) {
        this.captions = captions;
    }

    public void setPrivateGames(Map<String, Integer> privateGames) {
        this.privateGames = privateGames;
    }

    public void setBlockedUsers(Map<String, Integer> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int retrieveCaptionCount() {
        int captionCount = 0;
        if (captions != null) {
            captionCount = captions.size();
        }
        return captionCount;
    }

    public int retrieveCreatedGameCount() {
        int gameCount = 0;
        if (createdGames != null) {
            gameCount = createdGames.size();
        }
        return gameCount;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == getClass() && ((User) other).id.equals(id);
    }
}

