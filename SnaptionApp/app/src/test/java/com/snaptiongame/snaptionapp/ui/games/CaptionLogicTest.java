package com.snaptiongame.snaptionapp.ui.games;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.testobjects.TestCard;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Used to test logic for the captions.
 *
 * @author Cameron Geehr
 */

public class CaptionLogicTest {

    @Test
    public void testInsertCaption() {
        Card testCard1 = new Card("text");

        Caption caption1 = new Caption("1", "1", "1", testCard1, new ArrayList<String>());
        Caption caption2 = new Caption("2", "1", "1", testCard1, new ArrayList<String>());
        Caption caption3 = new Caption("3", "1", "1", testCard1, new ArrayList<String>());

        List<Caption> items = new ArrayList<>();
        items.add(caption2);

        int index1 = CaptionLogic.insertCaption(items, caption1);
        assertEquals(0, index1);

        int index2 = CaptionLogic.insertCaption(items, caption3);
        assertEquals(2, index2);
    }
}
