package com.snaptiongame.snaptionapp;

/**
 * Testing the Game class.
 * @author Cameron Geehr
 */

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.testobjects.TestCaption;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void verifyConstructors() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
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
        assertEquals(0, game.getJudgerRating());

        assertTrue(game.getCaptions().isEmpty());
        assertTrue(game.getIsOpen());
        for(String key : players.keySet()) {
            assertTrue(game.getPlayers().containsKey(key));
        }
        assertTrue(game.getCategories().containsAll(categories));
        assertTrue(game.getIsPublic());
    }

    @Test
    public void verifyAddCaption() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
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
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
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
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        game.setWinner("winner");
        assertEquals("winner", game.getWinner());
    }

    @Test
    public void verifyPeoplesChoice() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        game.setPeoplesChoice("winner");
        assertEquals("winner", game.getPeoplesChoice());
    }

    @Test
    public void verifyCloseGame() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        game.closeGame();
        assertFalse(game.getIsOpen());
    }

    @Test
    public void verifyUpvote() {
        Map<String, Integer> players = new HashMap<>();
        players.put("player1", 1);
        players.put("player2", 1);
        List<String> categories = new ArrayList<>();
        categories.add("lol");
        categories.add("kitten");
        long end = 1000;
        long start = 500;
        Game game = new Game("me", "you", "images/", players, categories, true, end, start, "G");

        game.upvote();
        assertEquals(1, game.getJudgerRating());
        game.upvote();
        assertEquals(2, game.getJudgerRating());
    }

}