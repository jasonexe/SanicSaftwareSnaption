package com.snaptiongame.snaptionapp;

/**
 * Testing the Game class.
 * @author Cameron Geehr
 */

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.testobjects.TestCard;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Tests the Caption class.
 *
 * @author Cameron Geehr
 */
public class CaptionTest {

    @Test
    public void verifyConstructors() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = new Card("text");
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);
        Caption caption2 = new Caption("1", "2", "3", testCard1, userInput1);
        Caption caption = new Caption();

        assertEquals("1", caption1.getId());
        assertEquals("2", caption1.getGameId());
        assertEquals("3", caption1.getUserId());
        assertEquals(testCard1, caption1.getCard());
        assertEquals(userInput1, caption1.getUserInput());
        assertEquals(new HashMap<String, Integer>(), caption1.getVotes());
        //can't run retrieveCaptionText because SpannableStringBuilder can't be used in tests
        assertEquals(0, caption1.retrieveNumVotes());
        assertEquals(caption1.getId(), caption1.toString());
        assertTrue(caption1.equals(caption2));
        assertFalse(caption1.equals(null));
        assertFalse(caption1.equals(caption));

        List<String> userInput2 = new ArrayList<>();
        userInput1.add("8");
        userInput1.add("9");

        try {
            Caption caption3 = new Caption("6", "7", testCard1, userInput2);
            // Has to fail because not logged in
            fail();
        } catch (Throwable e) {}

    }

    @Test
    public void verifyUser() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = TestCard.getTestCardSingleInput();
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);

        assertNull(caption1.retrieveUser());
        User user = new User("3", "something@something.com", "test", "5", "6", "image");
        caption1.assignUser(user);
        assertEquals(user, caption1.retrieveUser());
    }

    @Test
    public void verifyCompareTo() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = TestCard.getTestCardSingleInput();
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);
        Map<String, Integer> votes1 = new HashMap<>();
        votes1.put("1", 1);
        caption1.votes = votes1;

        assertEquals(1, caption1.retrieveNumVotes());

        List<String> userInput2 = new ArrayList<>();
        Card testCard2 = TestCard.getTestCardSingleInput();
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption2 = new Caption("2", "3", "4", testCard1, userInput1);
        caption2.votes = null;
        assertEquals(0, caption2.retrieveNumVotes());

        //test fewer votes
        assertTrue(caption1.compareTo(caption2) < 0);
        //test more votes
        assertTrue(caption2.compareTo(caption1) > 0);
        //test same votes with less id
        caption2.votes = votes1;
        assertTrue(caption1.compareTo(caption2) < 0);
        //test same votes with more id
        assertTrue(caption2.compareTo(caption1) > 0);
    }

}