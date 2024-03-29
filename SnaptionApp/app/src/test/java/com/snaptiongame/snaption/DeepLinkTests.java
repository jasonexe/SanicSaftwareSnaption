package com.snaptiongame.snaption;

import com.snaptiongame.snaption.servercalls.FirebaseDeepLinkCreator;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.games.GameActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DeepLinkTests {

    @Test
    public void CreateDeepLinkTest() {
        FirebaseDeepLinkCreator.getDeepLink("https://snaptiongame.com/public/games/-Kd9NH2qekgIvTnTr_-v", new ResourceListener<String>() {
            @Override
            public void onData(String data) {
                assertTrue("Got the wrong short link", data.contains("h883z.app.goo.gl/"));
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void DeepLinkerRejectsInvalidLink() {
        try {
            FirebaseDeepLinkCreator.getDeepLink("incorrectly formatted link", null);
            assertFalse("Deep linker should have thrown an error", true);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void InterpretDeepLinkNoGame() {
        FirebaseDeepLinkCreator.DeepLinkInfo info =
                FirebaseDeepLinkCreator.interpretDeepLinkString("https://snaptiongame.com");
        assertNull(info);
    }

    @Test
    public void InterpretDeepLinkWithGame() {
        String expectedId = "gameId";
        String expectedAccess = Constants.PUBLIC;
        FirebaseDeepLinkCreator.DeepLinkInfo info =
                FirebaseDeepLinkCreator.interpretDeepLinkString("https://snaptiongame.com/" +
                        "games/" + expectedAccess + "/" + expectedId);
        assertEquals(GameActivity.class, info.getClassForIntent());
        assertEquals(expectedId, info.getIntentString());
        assertEquals(expectedAccess, info.getAccessString());
    }

}