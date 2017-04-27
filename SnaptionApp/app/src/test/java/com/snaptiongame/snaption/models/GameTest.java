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
    GameMetaData gameMetaData1, gameMetaData2, gameMetaData3;
    GameData data1, data2, data3;
    Game game1, game2, game3;
    Map<String, Integer> players1, players2, players3;
    Map<String, Integer> tags1, tags2, tags3;
    Map<String, Integer> upvotes1, upvotes2, upvotes3;
    Map<String, Caption> captions1, captions2, captions3;

    @Before
    public void setup() {
        players1 = new HashMap<>();
        players2 = new HashMap<>();
        players3 = new HashMap<>();

        tags1 = new HashMap<>();
        tags2 = new HashMap<>();
        tags3 = new HashMap<>();

        upvotes1 = new HashMap<>();
        upvotes2 = new HashMap<>();
        upvotes3 = new HashMap<>();

        captions1 = new HashMap<>();
        captions2 = new HashMap<>();
        captions3 = new HashMap<>();

        String imagePath = "images/";

        gameMetaData1 = new GameMetaData("1", "a", imagePath, tags1, true, 1000, 500);
        gameMetaData2 = new GameMetaData("2", "b", imagePath, tags2, true, 2000, 500);
        gameMetaData3 = new GameMetaData("3", "c", imagePath, tags3, true, Long.MAX_VALUE);

        data1 = new GameData(players1, captions1);
        data2 = new GameData(players2, captions2);
        data3 = new GameData(players3, captions3);

        game1 = new Game(data1, gameMetaData1);
        game2 = new Game(data2, gameMetaData2);
        game3 = new Game(data3, gameMetaData3);
    }

    @Test
    public void verifyConstructors() {
        players1.put("player1", 1);
        players1.put("player2", 1);

        tags1.put("lol", 1);
        tags1.put("kitten", 1);

        //Nothing should be learned about empty constructor other than it doesn't crash
        Game game4 = new Game();

        assertEquals("1", game1.getId());
        assertEquals("a", game1.getPickerId());
        assertEquals("images/", game1.getImagePath());
        assertEquals(1000, game1.getEndDate());
        assertEquals(500, game1.getCreationDate());

        assertTrue(game1.getCaptions().isEmpty());
        assertFalse(game1.getIsOpen());
        assertTrue(game3.getIsOpen());
        for (String key : players1.keySet()) {
            assertTrue(game1.getPlayers().containsKey(key));
        }
        assertTrue(game1.getTags().equals(tags1));
        assertTrue(game1.getIsPublic());
    }

    @Test
    public void verifySetUpvotes() {
        upvotes1.put("1", 1);
        upvotes1.put("2", 1);

        game1.setUpvotes(upvotes1);
        assertEquals(upvotes1, game1.getUpvotes());
    }

    @Test
    public void verifySetImagePath() {
        game1.setImagePath("image");
        assertEquals("image", game1.getImagePath());
    }

    /*@Test
    public void verifyGetTopCaption() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = new Card("text");
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Map<String, Caption> game1Captions = new HashMap<>();
        Map<String, Caption> game2Captions = new HashMap<>();
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
        game1.setTopCaption(caption1);
        assertEquals(caption1, game1.getTopCaption());
    }*/

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