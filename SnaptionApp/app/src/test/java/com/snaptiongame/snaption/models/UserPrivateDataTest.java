package com.snaptiongame.snaption.models;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by austinrobarts on 4/22/17.
 */

public class UserPrivateDataTest {
    private UserPrivateData emptyData;
    private UserPrivateData nullData;
    private UserPrivateData filledData;

    @Before
    public void setup() {
        Map<String, Caption> captions = new HashMap<>();
        Map<String, Integer> createdGames = new HashMap<>();
        Map<String, String> joinedGames = new HashMap<>();

        captions.put("c1", new Caption());
        createdGames.put("g1", 1);
        createdGames.put("g2", 1);
        joinedGames.put("f1", "private");
        joinedGames.put("f2", "public");

        emptyData = new UserPrivateData();
        nullData = new UserPrivateData(null, null, null);
        filledData = new UserPrivateData(captions, createdGames, joinedGames);
    }

    @Test
    public void testConstructors() {
        assertEquals(emptyData.getCaptions(), null);
        assertEquals(emptyData.getCaptions(), null);
        assertEquals(emptyData.getCaptions(), null);
        assertEquals(nullData.getCaptions(), null);
        assertEquals(nullData.getCaptions(), null);
        assertEquals(nullData.getCaptions(), null);
        assertEquals(filledData.getCaptions().size(), 1);
        assertEquals(filledData.getCreatedGames().size(), 2);
        assertEquals(filledData.getJoinedGames().size(), 2);
    }

    @Test
    public void testSetters() {
        assertEquals(1, filledData.getCaptions().size());
        assertEquals(2, filledData.getCreatedGames().size());
        assertEquals(2, filledData.getJoinedGames().size());

        Map<String, Caption> captions = new HashMap<>();
        Map<String, Integer> createdGames = new HashMap<>();
        Map<String, String> joinedGames = new HashMap<>();

        captions.put("c1", new Caption());
        captions.put("c2", new Caption());
        createdGames.put("g1", 1);
        joinedGames.put("f1", "private");

        filledData.setCaptions(captions);
        assertEquals(2, filledData.getCaptions().size());
        filledData.setCreatedGames(createdGames);
        assertEquals(1, filledData.getCreatedGames().size());
        filledData.setJoinedGames(joinedGames);
        assertEquals(1, filledData.getJoinedGames().size());
    }
}
