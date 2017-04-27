package com.snaptiongame.snaption.models;

/**
 * Testing the Game class.
 * @author Cameron Geehr
 */

import com.snaptiongame.snaption.testobjects.TestCaption;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GameTest {

    @Before
    public void setup() {
        GameMetaData gameMetaData = new GameMetaData("1", "images/", new Map())
    }

    @Test
    public void verifyConstructors() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> tags = new HashMap<>();
        tags.put("lol", 1);
        tags.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, tags, true, end, "G");
        Game game1 = new Game("me", "you", "images/", players, tags, true, end, start, "G");
        //Nothing should be learned about empty constructor other than it doesn't crash
        Game game2 = new Game();

        assertEquals("me", game.getId());
        assertEquals("you", game.getPickerId());
        assertEquals("images/", game.getImagePath());
        assertEquals(end, game.getEndDate());
        assertEquals(start, game1.getCreationDate());
        assertEquals("G", game.getMaturityRating());

        assertTrue(game.getCaptions().isEmpty());
        assertTrue(game.getIsOpen());
        for(String key : players.keySet()) {
            assertTrue(game.getPlayers().containsKey(key));
        }
        assertTrue(game.getTags().equals(tags));
        assertTrue(game.getIsPublic());
    }

    @Test
    public void verifySetUpvotes() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> tags = new HashMap<>();
        tags.put("lol", 1);
        tags.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, tags, true, end, start, "G");

        Map<String, Integer> upvotes1 = new HashMap<>();
        upvotes1.put("1", 1);
        upvotes1.put("2", 1);

        game.setUpvotes(upvotes1);
        assertEquals(upvotes1, game.getUpvotes());
    }

    @Test
    public void verifyCloseGame() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> tags = new HashMap<>();
        tags.put("lol", 1);
        tags.put("kitten", 1);
        long end = Calendar.getInstance().getTimeInMillis();
        long start = 500;
        Game game = new Game("me", "you", "images/", players, tags, true, end, start, "G");

        game.closeGame();
        assertFalse(game.getIsOpen());
    }

    @Test
    public void verifyGetTopCaption() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = new Card("text");
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);
        Caption caption2 = new Caption("2", "3", "4", testCard1, userInput1);

        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> tags = new HashMap<>();
        tags.put("lol", 1);
        tags.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, tags, true, end, start, "G");

        //test empty caption list
        assertNull(game.getTopCaption());

        //test same amount of upvotes
        game.addCaption(caption1.getId(), caption1);
        game.addCaption(caption2.getId(), caption2);
        assertEquals(caption1, game.getTopCaption());

        //test different amount of upvotes
        Game game1 = new Game("me", "you", "images/", players, tags, true, end, start, "G");
        Map<String, Integer> upvotes1 = new HashMap<>();
        Map<String, Integer> upvotes2 = new HashMap<>();
        upvotes1.put("1", 1);
        upvotes1.put("2", 1);
        upvotes2.put("1", 1);
        caption2.upvotes = upvotes1;
        caption1.upvotes = upvotes2;
        game1.addCaption(caption1.getId(), caption1);
        game1.addCaption(caption2.getId(), caption2);
        assertEquals(caption2, game1.getTopCaption());

        //test winner selected
        Game game2 = new Game("me", "you", "images/", players, tags, true, Long.MAX_VALUE,
                start, "G");
        caption1.upvotes = upvotes1;
        game2.addCaption(caption1.getId(), caption1);
        game2.addCaption(caption2.getId(), caption2);
        assertEquals(caption1, game2.getTopCaption());

        //test winner already set
        game1.(caption1.getId());
        assertEquals(caption1, game1.getTopCaption());
    }

    @Test
    public void verifyTagsNull() {
        Game game = new Game();
        assertNull(game.getTags());
    }

    @Test
    public void verifyPlayersNull() {
        Game game = new Game();
        assertNull(game.getPlayers());
    }

    @Test
    public void verifyUpvotesNull() {
        Game game = new Game();
        assertNull(game.getUpvotes());
    }

    @Test
    public void verifyCaptionsNull() {
        Game game = new Game();
        assertNull(game.getCaptions());
    }
}