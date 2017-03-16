package com.snaptiongame.snaption.models;

/**
 * Testing the Game class.
 * @author Cameron Geehr
 */

import com.snaptiongame.snaption.testobjects.TestCaption;

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

    @Test
    public void verifyConstructors() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, "G");
        Game game1 = new Game("me", "you", "images/", players, categories, true, end, start, "G");
        //Nothing should be learned about empty constructor other than it doesn't crash
        Game game2 = new Game();

        assertEquals("me", game.getId());
        assertEquals("you", game.getPicker());
        assertEquals("images/", game.getImagePath());
        assertEquals(end, game.getEndDate());
        assertEquals(start, game1.getCreationDate());
        assertEquals("G", game.getMaturityRating());

        assertTrue(game.getCaptions().isEmpty());
        assertTrue(game.getIsOpen());
        for(String key : players.keySet()) {
            assertTrue(game.getPlayers().containsKey(key));
        }
        assertTrue(game.getCategories().equals(categories));
        assertTrue(game.getIsPublic());
    }

    @Test
    public void verifyAddCaption() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        assertEquals(0, game.getCaptions().size());

        Caption caption = TestCaption.getSingleInputCaption();
        game.addCaption("caption", caption);

        assertEquals(1, game.getCaptions().size());
        assertTrue(game.getCaptions().containsKey("caption"));
        assertEquals("TestId", ((Caption) game.getCaptions().get("caption")).getId());
    }

    @Test
    public void verifyAddPlayer() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        assertFalse(game.getPlayers().containsKey("player3"));
        game.addPlayer("player3");
        assertTrue(game.getPlayers().containsKey("player1"));
        assertTrue(game.getPlayers().containsKey("player2"));
        assertTrue(game.getPlayers().containsKey("player3"));
    }

    @Test
    public void verifySetWinner() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        game.setWinner("winner");
        assertEquals("winner", game.getWinner());
    }

    @Test
    public void verifySetImagePath() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        game.setImagePath("image");
        assertEquals("image", game.getImagePath());
    }

    @Test
    public void verifySetVotes() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        Map<String, Integer> votes1 = new HashMap<>();
        votes1.put("1", 1);
        votes1.put("2", 1);

        game.setVotes(votes1);
        assertEquals(votes1, game.getVotes());
    }

    @Test
    public void verifyCloseGame() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

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
        Map<String, Integer> categories = new HashMap<>();
        categories.put("lol", 1);
        categories.put("kitten", 1);
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        //test empty caption list
        assertNull(game.getTopCaption());

        //test same amount of upvotes
        game.addCaption(caption1.getId(), caption1);
        game.addCaption(caption2.getId(), caption2);
        assertEquals(caption1, game.getTopCaption());

        //test different amount of upvotes
        Game game1 = new Game("me", "you", "images/", players, categories, true, end, start, "G");
        Map<String, Integer> votes1 = new HashMap<>();
        Map<String, Integer> votes2 = new HashMap<>();
        votes1.put("1", 1);
        votes1.put("2", 1);
        votes2.put("1", 1);
        caption2.votes = votes1;
        caption1.votes = votes2;
        game1.addCaption(caption1.getId(), caption1);
        game1.addCaption(caption2.getId(), caption2);
        assertEquals(caption2, game1.getTopCaption());

        //test winner selected
        Game game2 = new Game("me", "you", "images/", players, categories, true, Long.MAX_VALUE,
                start, "G");
        caption1.votes = votes1;
        game2.addCaption(caption1.getId(), caption1);
        game2.addCaption(caption2.getId(), caption2);
        assertEquals(caption1, game2.getTopCaption());

        //test winner already set
        game1.setWinner(caption1.getId());
        assertEquals(caption1, game1.getTopCaption());
    }

    @Test
    public void verifyCategoriesNull() {
        Game game = new Game();
        assertNull(game.getCategories());
    }

    @Test
    public void verifyPlayersNull() {
        Game game = new Game();
        assertNull(game.getPlayers());
    }

    @Test
    public void verifyVotesNull() {
        Game game = new Game();
        assertNull(game.getVotes());
    }

    @Test
    public void verifyCaptionsNull() {
        Game game = new Game();
        assertNull(game.getCaptions());
    }
}