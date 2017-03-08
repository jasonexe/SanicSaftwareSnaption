package com.snaptiongame.snaptionapp.servercalls;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;
import com.snaptiongame.snaptionapp.utilities.BitmapConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import static android.R.attr.data;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinkCreator.KEY_STRING;
import static com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinkCreator.LINK_KEY;
import static com.snaptiongame.snaptionapp.utilities.BitmapConverter.drawableToBitmap;


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

public class FirebaseDeepLinkCreator {
    private static final String LINK_BEGINNING =  "https://snaptiongame.com"; // Use this at beginning of links

    static final String KEY_STRING = "/v1/shortLinks?key=AIzaSyAa9WDzfmNN5j3i8jn0smpHkZypMmxFCMI";
    static final String LINK_KEY = "longDynamicLink";

    // Our app's firebase dynamic link domain
    private static final String DYNAMIC_LINK_DOMAIN = "https://ba63n.app.goo.gl/";
    // The name of the file that holds the game preview to send in the intent
    private static final String FILE_NAME = "gamePreview.jpg";
    // URL to firebase's dynamic shortlink generator
    private static final String SHORT_LINK_GENERATOR_URL = "https://firebasedynamiclinks.googleapis.com";
    // Our app's default android package
    private static final String ANDROID_PACKAGE = "com.snaptiongame.snaptionapp";
    // The iOS app's default package. Taken from firebase
    private static final String IOS_PACKAGE = "edu.calpoly.csc.2168.snapsquad.verticalprototype";
    // The key for the shortlink to send in the intent if needed
    private static final String SHORTLINK_KEY = "shortLink";
    private static final String INTENT_IMAGE_TYPE = "image/jpeg";
    private static RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(SHORT_LINK_GENERATOR_URL) // Firebase's short link generator url
            .build();
    private static DeepLink linkJSON = adapter.create(DeepLink.class);

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

    /**
     * Creates an intent for inviting people to a specific game through
     * apps that accept the ACTION_SEND intent.
     * @param activity The activity users are inviting their friends from
     * @param game The game friends should get a deep link to
     * @param progressView The progress bar to be displayed while loading the image (optional)
     * @param image Bitmap containing the image to send in the intent
     */
    public static void createGameInviteIntent(final FragmentActivity activity,
                                              final Game game,
                                              final View progressView,
                                              final Bitmap image,
                                              final String sampleCaption) {
        if(progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
        String linkDestination = LINK_BEGINNING + "/games/" + game.getId();
        // First, create the deep link to this specific game
        getDeepLink(linkDestination, new ResourceListener<String>() {
            @Override
            public void onData(String shortLink) {
                File file = new File(activity.getExternalCacheDir(), FILE_NAME);
                Intent toStart = new Intent(Intent.ACTION_SEND);
                // Put in stuff we're guaranteed to have in the intent, the message and title
                toStart.setType(INTENT_IMAGE_TYPE);
                toStart.putExtra(Intent.EXTRA_SUBJECT, R.string.join_snaption_subject);
                toStart.putExtra(Intent.EXTRA_TEXT, String.format(activity.getResources()
                        .getString(R.string.join_game_email_body),
                        sampleCaption, shortLink));
                FileOutputStream out = null;
                // If there is actually an image, do the converting stuff
                if(image != null) {
                    try {
                        out = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        toStart.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        out.close();
                    } catch (IOException e) {
                        // Don't have to worry too much about errors here, since we'll just keep
                        // going just not have the image in the invite
                        e.printStackTrace();
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                activity.startActivity(Intent.createChooser(toStart, activity
                        .getResources().getString(R.string.game_invite)));
                if(progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
    }


}
