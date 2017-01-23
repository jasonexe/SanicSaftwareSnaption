package com.snaptiongame.snaptionapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by austinrobarts on 1/21/17.
 */

public class User {

    private String id;
    private String email;
    private String displayName;
    private List<String> friends;
    private List<String> games;
    private Map<String, Caption> captions;
    private String notificationId;
    private String facebookId;

    //needed for firebase compatibility
    public User() {}

    public User(String id, String email, String displayName, String notificationId, String facebookId) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.notificationId = notificationId;
        this.facebookId = facebookId;

        friends = new ArrayList<>();
        games = new ArrayList<>();
        captions = new HashMap<>();
    }

    public User(String id, String email, String displayName, String notificationId,
                String facebookId, List<String> friends, List<String> games, Map<String, Caption> captions) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.notificationId = notificationId;
        this.facebookId = facebookId;

        this.friends = friends;
        this.games = games;
        this.captions = captions;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getGames() {
        return games;
    }

    public Map<String, Caption> getCaptions() {
        return captions;
    }

    public String getDisplayName() {
        return displayName;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void setGames(List<String> games) {
        this.games = games;
    }

    public void setCaptions(Map<String, Caption> captions) {
        this.captions = captions;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}

