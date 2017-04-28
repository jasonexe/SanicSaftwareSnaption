package com.snaptiongame.snaption.models;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by austinrobarts on 4/22/17.
 */
public class UserPublicDataTest {

    private UserPublicData emptyData;
    private UserPublicData nullData;
    private UserPublicData filledData;

    @Before
    public void setup() {
        Map<String, Caption> captions = new HashMap<>();
        Map<String, Integer> createdGames = new HashMap<>();
        Map<String, Integer> friends = new HashMap<>();

        captions.put("c1", new Caption());
        createdGames.put("g1", 1);
        createdGames.put("g2", 1);
        friends.put("f1", 1);
        friends.put("f2", 1);

        emptyData = new UserPublicData();
        nullData = new UserPublicData(null, null, null);
        filledData = new UserPublicData(captions, createdGames, friends);
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
        assertEquals(filledData.getFriends().size(), 2);
    }

    @Test
    public void testSetters() {
        assertEquals(1, filledData.getCaptions().size());
        assertEquals(2, filledData.getCreatedGames().size());
        assertEquals(2, filledData.getFriends().size());

        Map<String, Caption> captions = new HashMap<>();
        Map<String, Integer> createdGames = new HashMap<>();
        Map<String, Integer> friends = new HashMap<>();

        captions.put("c1", new Caption());
        captions.put("c2", new Caption());
        createdGames.put("g1", 1);
        friends.put("f1", 1);

        filledData.setCaptions(captions);
        assertEquals(2, filledData.getCaptions().size());
        filledData.setCreatedGames(createdGames);
        assertEquals(1, filledData.getCreatedGames().size());
        filledData.setFriends(friends);
        assertEquals(1, filledData.getFriends().size());
    }
}
