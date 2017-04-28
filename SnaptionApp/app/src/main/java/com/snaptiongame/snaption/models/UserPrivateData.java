package com.snaptiongame.snaption.models;

import java.util.Map;

/**
 * Contains private data of user like private games, captions, and joinedGames
 * Fits Firebase data structure
 * Created by austinrobarts on 4/22/17.
 */

public class UserPrivateData {

    private Map<String, Caption> captions;
    private Map<String, Integer> createdGames;
    private Map<String, String> joinedGames;

    public UserPrivateData() {}

    public UserPrivateData(Map<String, Caption> captions, Map<String, Integer> createdGames, Map<String, String> joinedGames) {
        this.captions = captions;
        this.createdGames = createdGames;
        this.joinedGames = joinedGames;
    }

    public UserPrivateData(UserPrivateData other) {
        if (other != null) {
            this.captions = other.captions;
            this.createdGames = other.createdGames;
            this.joinedGames = other.joinedGames;
        }
    }

    public Map<String, Caption> getCaptions() {
        return captions;
    }

    public Map<String, Integer> getCreatedGames() {
        return createdGames;
    }

    public Map<String, String> getJoinedGames() {
        return joinedGames;
    }

    public void setCaptions(Map<String, Caption> captions) {
        this.captions = captions;
    }

    public void setCreatedGames(Map<String, Integer> createdGames) {
        this.createdGames = createdGames;
    }

    public void setJoinedGames(Map<String, String> joinedGames) {
        this.joinedGames = joinedGames;
    }
}
