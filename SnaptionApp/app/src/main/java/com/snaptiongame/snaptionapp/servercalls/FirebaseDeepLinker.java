package com.snaptiongame.snaptionapp.servercalls;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinker.KEY_STRING;
import static com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinker.LINK_KEY;


/**
 * Class that will create and interpret deep links.
 * Created by Jason Krein on 2/16/2017.
 */

interface DeepLink {
    @FormUrlEncoded
    @POST(KEY_STRING)
    void getShortLink(
            @Field(LINK_KEY) String link,
            Callback<Response> callback);
}

public class FirebaseDeepLinker {
    public static final String LINK_BEGINNING =  "https://snaptiongame.com"; // Use this at beginning of links

    static final String KEY_STRING = "/v1/shortLinks?key=AIzaSyAa9WDzfmNN5j3i8jn0smpHkZypMmxFCMI";
    static final String LINK_KEY = "longDynamicLink";

    private static final String DYNAMIC_LINK_DOMAIN = "https://ba63n.app.goo.gl/";
    private static final String SHORT_LINK_GENERATOR_URL = "https://firebasedynamiclinks.googleapis.com";
    private static final String ANDROID_PACKAGE = "com.snaptiongame.snaptionapp";
    private static final String IOS_PACKAGE = "edu.calpoly.csc.2168.snapsquad.verticalprototype";
    private static final String SHORTLINK_KEY = "shortLink";


    /**
     * Generates a firebase dynamic short link, that allows users to click it and be linked to
     * our app.
     * @param expectedLink The link expected by the app. Should start with https://snaptiongame.com
     *                     and from there be specific. IE: to link to friends page, you can make
     *                     it be https://snaptiongame.com/friends or to link to a game,
     *                     https://snaptiongame.com/games/_gameId_
     * @return The short link that a user can click to get to the app.
     */
    public static void getDeepLink(String expectedLink, final ResourceListener<String> listener)
            throws IllegalArgumentException {
        // Make sure the link is valid
        if(!expectedLink.contains(LINK_BEGINNING)) {
            throw new IllegalArgumentException("Incorrectly formatted link. Must have " +
                    LINK_BEGINNING + " in it, otherwise short link won't work");
        }

        // Start of the link. This is retrieved from Firebase Dynamic Links page
        String longDynamicLink = DYNAMIC_LINK_DOMAIN;
        // Add the link the app is looking for. This is the special sauce
        longDynamicLink += "?link=" + expectedLink;
        // Name of the Android package that is linked with Firebase
        longDynamicLink += "&apn=" + ANDROID_PACKAGE;
        // Name of the IOS package linked with Firebase
        longDynamicLink += "&ibi=" + IOS_PACKAGE;

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SHORT_LINK_GENERATOR_URL) // Firebase's short link generator url
                .build();

        DeepLink linkJSON = adapter.create(DeepLink.class);
        linkJSON.getShortLink(
                longDynamicLink,
                createDeepLinkCallback(listener)
        );
    }

    // Gets the callback to be used by retrofit
    static private Callback<Response> createDeepLinkCallback(final ResourceListener<String> listener) {
        return new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                // Read output using bufferedReader
                BufferedReader reader = null;
                String output = "";
                try {
                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                    String nextLine = reader.readLine();
                    while(nextLine != null) {
                        output += nextLine;
                        nextLine = reader.readLine();
                    }
                    // Parse the result into JSON, and then get the header for short link
                    JsonParser parser = new JsonParser();
                    JsonObject json = (JsonObject) parser.parse(output);
                    String shortLink = json.get(SHORTLINK_KEY).getAsString();
                    listener.onData(shortLink);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.toString());
            }
        };
    }

    // Contains anything that could be needed for deep linking.
    // If classForIntent = GameActivity.class, then get the intentGame and string to put in
    // could be other stuff, maybe.
    public static class DeepLinkInfo {
        private Class classForIntent;
        private String intentString;

        public DeepLinkInfo(Class classForIntent) {
            this.classForIntent = classForIntent;
        }

        public void setIntentString(String intentString) {
            this.intentString = intentString;
        }

        public Class getClassForIntent() {
            return classForIntent;
        }


        public String getIntentString() {
            return intentString;
        }
    }

    @Nullable
    public static DeepLinkInfo interpretDeepLinkString(String deepLink) {
        // If it has "games" in the url, it'll be a deep link with the game ID as the last thing
        if(deepLink.contains("games")) {
            Class toSend = GameActivity.class;
            String gameId = deepLink.substring(deepLink.lastIndexOf("/") + 1);
            DeepLinkInfo info = new DeepLinkInfo(toSend);
            info.setIntentString(gameId);
            return info;
        } else {
            return null;
        }
    }
}
