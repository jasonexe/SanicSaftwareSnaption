package com.snaptiongame.snaptionapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUpload;
import com.snaptiongame.snaptionapp.servercalls.FirebaseListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.R.id.message;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

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
        MessageUpdater updater = new MessageUpdater() {
            @Override
            public void onUpdate(Object test) {
                assertEquals("Heyo", test.toString());
                FirebaseUpload.deleteValue("testing/message");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertNull(test);
            }

        };
        FirebaseUpload.uploadString("testing/message", "Heyo");
        //Need this to upload
        Thread.sleep(500);
        FirebaseListener testListener = new FirebaseListener("testing/message", updater);
    }
}
