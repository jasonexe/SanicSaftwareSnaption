package com.snaptiongame.snaption.models;

/**
 * Testing the Game class.
 * @author Cameron Geehr
 */

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.snaptiongame.snaption.R;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GameTest {
    private GameMetadata gameMetadata1, gameMetadata2, gameMetadata3;
    private GameData data1, data2, data3;
    private Game game1, game2, game3;
    private Map<String, Integer> players1, players2, players3;
    private Map<String, Integer> tags1, tags2, tags3;
    private Map<String, Integer> upvotes1, upvotes2, upvotes3;
    private Map<String, Caption> captions1, captions2, captions3;

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

        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, 1000, 500, 1.0);
        gameMetadata2 = new GameMetadata("2", "b", imagePath, tags2, true, 2000, 500, 1.0);
        gameMetadata3 = new GameMetadata("3", "c", imagePath, tags3, true, Long.MAX_VALUE, 1.0);

        data1 = new GameData(players1, captions1);
        data2 = new GameData(players2, captions2);
        data3 = new GameData(players3, captions3);

        game1 = new Game(data1, gameMetadata1);
        game2 = new Game(data2, gameMetadata2);
        game3 = new Game(data3, gameMetadata3);
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

        assertEquals(gameMetadata1.getCreationDate(), game1.getMetaData().getCreationDate());
        captions1.put("1", caption);
        assertEquals(data1.getCaptions(), game1.getData().getCaptions());

        assertEquals(1.0, gameMetadata1.getImageAspectRatio(), .00001);
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
        GameMetadata metaData = new GameMetadata();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getTags());
    }

    @Test
    public void verifyPlayersNull() {
        GameMetadata metaData = new GameMetadata();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getPlayers());
    }

    @Test
    public void verifyUpvotesNull() {
        GameMetadata metaData = new GameMetadata();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getUpvotes());
    }

    @Test
    public void verifyCaptionsNull() {
        GameMetadata metaData = new GameMetadata();
        GameData gameData = new GameData();
        Game game = new Game(gameData, metaData);
        assertNull(game.getCaptions());
    }

    @Test
    public void verifyTimeRemaining() {
        players1 = new HashMap<>();
        tags1 = new HashMap<>();
        upvotes1 = new HashMap<>();
        captions1 = new HashMap<>();
        int end = 60;
        int start = 10;
        String imagePath = "images/";
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        data1 = new GameData(players1, captions1);
        int[] format = {1, R.string.minutes};
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 40;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 0;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 3599;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 59;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 3598;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 3000;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 50;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 3600;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 1;
        format[1] = R.string.hours;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 3660;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 86399;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 23;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 72000;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 20;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 86400;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 1;
        format[1] = R.string.days;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 86440;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));

        end = 4320000;
        gameMetadata1 = new GameMetadata("1", "a", imagePath, tags1, true, end, start, 1.0);
        format[0] = 50;
        assertTrue(Arrays.equals(format, gameMetadata1.remainingTime(0)));
    }
}