package com.snaptiongame.snaption.utilities;


import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 * Created by brittanyberlanga on 4/29/17.
 */

public class ViewUtilitiesTest {
    @Test
    public void calculateImageHeightTest() {
        assertEquals(500, ViewUtilities.calculateViewHeight(1, 500, 800));
        assertEquals(800, ViewUtilities.calculateViewHeight(1, 1000, 800));
        assertEquals(333, ViewUtilities.calculateViewHeight(1.5, 500, 800));
    }
}
