package com.snaptiongame.snaptionapp;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ModelTests {
    @Test
    public void cardTest() throws  Exception {
        ArrayList<String> inputArr = new ArrayList<String>();
        inputArr.add("Yay");
        inputArr.add("Cards work");
        String cardText = "%s! %s! I like ice cream";
        Card testCard = new Card(cardText);
        assertEquals("Yay! Cards work! I like ice cream", testCard.getCardWithUserInput(inputArr));
    }


}