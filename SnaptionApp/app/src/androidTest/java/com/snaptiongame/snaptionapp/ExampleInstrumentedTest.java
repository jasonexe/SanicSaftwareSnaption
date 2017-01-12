package com.snaptiongame.snaptionapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.snaptiongame.snaptionapp.servercalls.FirebaseUpload;
import com.snaptiongame.snaptionapp.servercalls.MessageListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

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
    public void testDownload() {
        MessageListener notExist = new MessageListener("games");
        assertEquals("Nothun", notExist.getMessage());
    }
}
