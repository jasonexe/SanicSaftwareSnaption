package com.snaptiongame.snaption.ui.games;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by brittanyberlanga on 4/28/17.
 */

public class GameActivityTest {
    @Test
    public void calculateImageHeightTest() {
        assertEquals(500, GameActivity.calculateImageHeight(500, 1, 800));
        assertEquals(800, GameActivity.calculateImageHeight(1000, 1, 800));
        assertEquals(333, GameActivity.calculateImageHeight(500, 1.5, 800));
    }
}
