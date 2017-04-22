package com.snaptiongame.snaption.models;

import java.util.Map;

/**
 * A model class for the User object.
 *
 * @author Austin Robarts
 */
public class User implements Person, Comparable<User> {

    private Map<String, Integer> friends;
    private Map<String, Integer> createdGames;
    private Map<String, Caption> captions;
    private Map<String, Integer> blockedUsers; // Ids of users this user has blocked
    private Map<String, Integer> privateGames; // Private games this user was invited to
    private UserMetadata metadata;
    private UserPrivateData privateData;
    private UserPublicData publicData;

    //needed for firebase compatibility
    public User() {}

    public User(UserMetadata metadata, UserPublicData publicData, UserPrivateData privateData) {
        this.metadata = metadata;
        this.publicData = publicData;
        this.privateData = privateData;
    }

    public User(String id, String email, String displayName, String notificationId, String facebookId, String imagePath) {
        metadata = new UserMetadata(displayName, email, imagePath, notificationId, facebookId, id);

        privateData = new UserPrivateData();
        publicData = new UserPublicData();
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
        publicData.setFriends(friends);
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

    public String retrieveDisplayName() {
        return metadata.getDisplayName();
    }

    public String getLowercaseDisplayName() {
        metadata.getSearchName();
    }

    public String getEmail() {
        return metadata.getEmail();
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getId() {
        return metadata.getId();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(boolean android) {
        isAndroid = android;
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

    public void setLowercaseDisplayName(String name) {
        // NoOp, lowercase display name is based on the normal display name. At least we don't
        // get errors anymore
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

    public int retrieveFriendsCount() {
        int friendCount = 0;
        if(friends != null) {
            friendCount = friends.size();
        }
        return friendCount;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        return other.getClass() == getClass() && ((User) other).id.equals(id);
    }

    @Override
    public int compareTo(User other) {
        int result = getLowercaseDisplayName().compareTo(other.getLowercaseDisplayName());
        if (result == 0) {
            result = email.compareTo(other.getEmail());
        }
        return result;
    }
}

