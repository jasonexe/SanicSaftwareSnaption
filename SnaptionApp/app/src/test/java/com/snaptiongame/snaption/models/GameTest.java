package com.snaptiongame.snaption.models;

/**
 * Testing the Game class.
 * @author Cameron Geehr
 */

import com.snaptiongame.snaption.testobjects.TestCaption;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
        List<String> userInput = new ArrayList<>();
        Card testCard = new Card("text");
        testCard.setId("0");
        userInput.add("4");
        userInput.add("5");
        Caption caption = new Caption("1", "2", "3", testCard, userInput);

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
        assertFalse(game1.isOpen());
        assertTrue(game3.isOpen());
        for (String key : players1.keySet()) {
            assertTrue(game1.getPlayers().containsKey(key));
        }
        assertTrue(game1.getTags().equals(tags1));
        assertTrue(game1.getIsPublic());

        assertEquals(gameMetaData1.getCreationDate(), game1.getMetaData().getCreationDate());
        captions1.put("1", caption);
        assertEquals(data1.getCaptions(), game1.getData().getCaptions());
    }

    @Test
    public void verifySetUpvotes() {
        upvotes1.put("1", 1);
        upvotes1.put("2", 1);

        game1.setUpvotes(upvotes1);
        assertEquals(upvotes1, game1.getUpvotes());
    }

    @Test
    public void verifySetCaptions() {
        List<String> userInput = new ArrayList<>();
        Card testCard = new Card("text");
        testCard.setId("0");
        userInput.add("4");
        userInput.add("5");
        Caption caption = new Caption("1", "2", "3", testCard, userInput);
        captions2.put("1", caption);

        game1.setCaptions(captions2);
        assertEquals(captions2, game1.getCaptions());
    }

    @Test
    public void verifySetPlayers() {
        players2.put("id", 1);
        game1.setPlayers(players2);

        assertEquals(players2, game1.getPlayers());
    }

    @Test
    public void verifyGetTopCaption() {
        List<String> userInput = new ArrayList<>();
        Card testCard = new Card("text");
        testCard.setId("0");
        userInput.add("4");
        userInput.add("5");
        Caption caption = new Caption("1", "2", "3", testCard, userInput);
        game1.setTopCaption(caption);

        assertEquals(caption, game1.getTopCaption());
    }

    @Test
    public void verifyTagsNull() {
        GameMetaData metaData = new GameMetaData();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getTags());
    }

    @Test
    public void verifyPlayersNull() {
        GameMetaData metaData = new GameMetaData();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getPlayers());
    }

    @Test
    public void verifyUpvotesNull() {
        GameMetaData metaData = new GameMetaData();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getUpvotes());
    }

    @Test
    public void verifyCaptionsNull() {
        GameMetaData metaData = new GameMetaData();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getCaptions());
    }
}