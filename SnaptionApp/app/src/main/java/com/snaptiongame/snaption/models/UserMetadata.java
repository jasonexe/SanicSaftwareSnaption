package com.snaptiongame.snaption.models;

import java.io.Serializable;

/**
 * Class containing all basic user information available to public
 * Fits Firebase data structure
 * Created by austinrobarts on 4/22/17.
 */

public class UserMetadata implements Person, Comparable<UserMetadata>, Serializable {

    private String displayName;
    private String email;
    private String imagePath;
    private String notificationId;
    private String facebookId;
    private boolean isAndroid;
    private String id;

    public UserMetadata() {}

    public UserMetadata(String displayName, String email, String imagePath, String notificationId, String facebookId, String id) {
        this.displayName = displayName;
        this.email = email;
        this.imagePath = imagePath;
        this.notificationId = notificationId;
        this.facebookId = facebookId;
        this.id = id;
        isAndroid = true;
    }

    public UserMetadata(UserMetadata other) {
        if (other != null) {
            this.displayName = other.displayName;
            this.email = other.email;
            this.imagePath = other.imagePath;
            this.notificationId = other.notificationId;
            this.facebookId = other.facebookId;
            this.id = other.id;
            isAndroid = true;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSearchName() {
        return displayName.toLowerCase();
    }

    public String getEmail() {
        return email;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public boolean getIsAndroid() {
        return isAndroid;
    }

    public String getId() {
        return id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSearchName(String searchName) {
        //NoOP can only set display name not search name
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setIsAndroid(boolean android) {
        isAndroid = android;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(UserMetadata other) {
        int result = getSearchName().compareTo(other.getSearchName());
        if (result == 0) {
            result = getEmail().compareTo(other.getEmail());
        }
        return result;
    }
}
