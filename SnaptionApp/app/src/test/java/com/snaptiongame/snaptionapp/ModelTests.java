package com.snaptiongame.snaptionapp;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinker;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

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
    public void DeepLinkTest() {

        FirebaseDeepLinker.getDeepLink("https://snaptiongame.com/games/-Kd9NH2qekgIvTnTr_-v", new ResourceListener<String>() {
            @Override
            public void onData(String data) {
                System.out.println(data);
                assertTrue("Got the wrong short link", data.contains("ba63n.app.goo.gl"));
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

}