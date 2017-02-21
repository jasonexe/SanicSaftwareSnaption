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
    public static final String LINK_BEGINNING =  "https://snaptiongame.com"; // Use this at beginning of links

    static final String KEY_STRING = "/v1/shortLinks?key=AIzaSyAa9WDzfmNN5j3i8jn0smpHkZypMmxFCMI";
    static final String LINK_KEY = "longDynamicLink";

    private static final String DYNAMIC_LINK_DOMAIN = "https://ba63n.app.goo.gl/";
    private static final String SHORT_LINK_GENERATOR_URL = "https://firebasedynamiclinks.googleapis.com";
    private static final String ANDROID_PACKAGE = "com.snaptiongame.snaptionapp";
    private static final String IOS_PACKAGE = "edu.calpoly.csc.2168.snapsquad.verticalprototype";
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

    public static void createGameInviteIntent(final FragmentActivity activity, final Game game, final View progressView, final ImageView image) {
        progressView.setVisibility(View.VISIBLE);
        String linkDestination = LINK_BEGINNING + "/games/" + game.getId();
        // First, create the deep link to this specific game
        getDeepLink(linkDestination, new ResourceListener<String>() {
            @Override
            public void onData(String shortLink) {
                File file = new File(activity.getExternalCacheDir(), "gamePreview.jpg");
                Intent toStart = new Intent(Intent.ACTION_SEND);
                toStart.setType(INTENT_IMAGE_TYPE);
                toStart.putExtra(Intent.EXTRA_SUBJECT, R.string.join_snaption_subject);
                toStart.putExtra(Intent.EXTRA_TEXT, String.format(activity.getResources()
                        .getString(R.string.join_snaption_email_body), shortLink));
                FileOutputStream out = null;
                try {
                    //Take the image out of the imageView instead of downloading from Firebase again
                    Bitmap bmp = drawableToBitmap(image.getDrawable());
                    out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    toStart.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    out.close();
                } catch (IOException e) {
                    // Don't have to worry too much about errors here, since we'll just keep
                    // going just not have the image in the invite
                    e.printStackTrace();
                } finally {
                    if(out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                activity.startActivity(Intent.createChooser(toStart, activity
                        .getResources().getString(R.string.game_invite)));
                progressView.setVisibility(View.GONE);
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
    }

    // StackOverflow code to convert drawable to a bitmap
    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
