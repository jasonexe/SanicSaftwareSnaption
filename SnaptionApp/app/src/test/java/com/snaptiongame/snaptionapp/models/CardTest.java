package com.snaptiongame.snaptionapp.models;

import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.testobjects.TestCard;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jason_000 on 3/13/2017.
 */

public class CardTest {

    @Test
    public void testConstructTestCard() {
        TestCard card = new TestCard();
        assertTrue(true);
    }

    @Test
    public void testGetFirstHalf() {
        Card card = new Card("First half %s ahahahah");
        assertEquals("First half ", card.retrieveFirstHalfText());
    }

    @Test
    public void testGetSecondHalf() {
        Card card = new Card("alkgjalkgjalj %s second half!");
        assertEquals(" second half!", card.retrieveSecondHalfText());
    }

    @Test
    public void testEqualToNonCard() {
        Card card = new Card();
        assertFalse(card.equals("a string"));
    }
}
