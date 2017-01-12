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

    @Test
    public void testUploading() {
        FirebaseUpload testUpload = new FirebaseUpload();
        testUpload.uploadString("TestUpload", "Testing");
    }

    @Test
    public void testDownload() throws InterruptedException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("testing/path");
        myRef.setValue("another test");
//        assertEquals(FirebaseUpload.uploadString("Test/hkhj", "Heyo"), true);
        MessageUpdater updater = new MessageUpdater() {
            @Override
            public void onUpdate(Object test) {
                assertEquals("Heyo", test.toString());
            }
        };
        //Need this to upload
        Thread.sleep(1000);
        FirebaseListener testListener = new FirebaseListener("Test", updater);
    }
}
