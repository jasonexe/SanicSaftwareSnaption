package com.snaptiongame.snaption.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class containing all basic game information, and fits Firebase data structure.
 *
 * @author Cameron Geehr
 */

public class GameData implements Serializable {

    private Map<String, Integer> players;
    private Map<String, Caption> captions;

    /**
     * Default constructor for Firebase.
     */
    public GameData() {}

    public GameData(Map players, Map captions) {
        this.players = players;
        this.captions = captions;
    }

    public Map<String, Caption> getCaptions() {
        if (captions == null) {
            return null;
        }
        return new HashMap<>(captions);
    }

    public Map<String, Integer> getPlayers() {
        if (players == null) {
            return null;
        }
        return new HashMap<>(players);
    }

    public void addPlayer(String playerId) {
        if(players == null) {
            players = new TreeMap<>();
        }
        players.put(playerId, 1);
    }

    public void setCaptions(Map<String, Caption> captions) {
        this.captions = captions;
    }

    public void setPlayers(Map<String, Integer> players) {
        this.players = players;
    }
}
