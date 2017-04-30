package com.snaptiongame.snaption.models;

import java.util.Map;

/**
 * UserPublicData holds all data that can be publicly shared from a user to the community.
 * This includes friends, captions on public games, and public games the user created
 *
 * Created by austinrobarts on 4/22/17.
 */

public class UserPublicData {
    private Map<String, Caption> captions; //captions user has added to public games
    private Map<String, Integer> createdGames; //public games user has created
    private Map<String, Integer> friends; //friends the user has added

    public UserPublicData() {}

    public UserPublicData(Map<String, Caption> captions, Map<String, Integer> createdGames, Map<String, Integer> friends) {
        this.captions = captions;
        this.createdGames = createdGames;
        this.friends = friends;
    }

    public UserPublicData(UserPublicData other) {
        if (other != null) {
            this.captions = other.captions;
            this.createdGames = other.createdGames;
            this.friends = other.friends;
        }
    }

    public Map<String, Caption> getCaptions() {
        return captions;
    }

    public Map<String, Integer> getCreatedGames() {
        return createdGames;
    }

    public Map<String, Integer> getFriends() {
        return friends;
    }

    public void setCaptions(Map<String, Caption> captions) {
        this.captions = captions;
    }

    public void setCreatedGames(Map<String, Integer> createdGames) {
        this.createdGames = createdGames;
    }

    public void setFriends(Map<String, Integer> friends) {
        this.friends = friends;
    }
}
