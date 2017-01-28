package com.snaptiongame.snaptionapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseTests {

    Context appContext;
    @Test
    public void useAppContext() throws Exception {
        System.err.println("Test 1");
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.snaptiongame.snaptionapp", appContext.getPackageName());
    }

    //This asserts both uploads and download work. Kind of.
    @Test
    public void testDownload() throws InterruptedException {
        //TODO this should login first. Right now won't work if db is only changeable with auth.
        ResourceListener updater = new ResourceListener<String>() {
            @Override
            public void onData(String test) {
                assertEquals("Heyo", test);
                FirebaseUploader.deleteValue("testing/message");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertNull(test);
            }
            @Override
            public Class getDataType() {
                return String.class;
            }
        };
        FirebaseUploader.uploadObject("testing/message", "Heyo");
        Thread.sleep(500); //Need this to upload
        new FirebaseResourceManager().retrieveSingleWithUpdates("testing/message", updater);
    }

    @Test
    public void cantCaptionWhenNotLoggedInTest() throws Exception {
        ArrayList<String> inputArr = new ArrayList<String>();
        inputArr.add("Yay");
        inputArr.add("Cards work");
        FirebaseAuth.getInstance().signOut();
        try {
            Caption testCaption = new Caption("TestId", "TestGameId", "TestUserId", new Card("Whatevs"), inputArr);
            assertTrue("User was allowed to submit a caption when not logged in", false);
        } catch (IllegalStateException e){
            assertTrue(true);
        }
    }

    // Need to run this one as an instrumented test since Caption uses SpannableStringBuilder,
    // which is an Android resource
    @Test
    public void captionTest() throws  Exception {
        ArrayList<String> inputArr = new ArrayList<String>();
        inputArr.add("Yay");
        inputArr.add("Cards work");
        String cardText = "%s! %s! I like ice cream";
        Card testCard = new Card(cardText);
        Caption testCaption = new Caption("TestId", "TestGameId", "Test user", testCard, inputArr);
        assertEquals("Yay! Cards work! I like ice cream", testCaption.retrieveCaptionText().toString());
    }

    @Test
    public void addStuffToFirebase() throws Exception {

    }
}
