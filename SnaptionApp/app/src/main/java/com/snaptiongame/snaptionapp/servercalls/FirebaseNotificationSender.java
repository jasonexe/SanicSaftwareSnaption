package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by austinrobarts on 3/8/17.
 */

public class FirebaseNotificationSender {
    private static final String FIREBASE_SERVER_KEY = "AAAA1YbN64o:APA91bFkAACOweZYo_FRyN6lIVKEvAoNstDavdLgXPjm4c74WN71kmCQjfR0m6bVaktnejgbbuaAyZp-vWclxv6-sZjm8iW9oyfqTep4fsuA5gZAfPYXJxI5vmkNd5Zzb3d2-p6nchpkcM-go2DfwSXn-BFF9fKTFg\n";
    private static final String FIREBASE_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String POST = "POST";
    private static final String JSON_TO = "to";
    private static final String JSON_DATA = "data";
    private static final String JSON_NOTIFICATION_KEY = "notification";
    private static final String JSON_TITLE = "title";
    private static final String JSON_BODY = "body";
    private static final String JSON_PRIORITY_KEY = "priority";
    private static final String JSON_PRIORITY_VAL = "high";
    private static final String JSON_BADGE_KEY = "badge";
    private static final String JSON_BADGE_VAL = "enabled";
    private static final String JSON_AUTH = "Authorization";
    private static final String JSON_AUTH_KEY = "key=";
    private static final String JSON_CONTENT_TYPE = "Content-Type";
    private static final String JSON_CONTENT_VAL = "application/json";
    private static final String IOS_NOTIFICATION_BODY = "%s added you to a game!";
    private static final String IOS_NOTIFICATION_TITLE = "Snaption";

    /**
     * Sends a Notification to a user that they have been invited to a game
     *
     * @param to the user to receive the notification
     * @param from the user sending the invite
     * @param gameId the id of the game they are invited to
     */
    public static void sendGameCreationNotification(final User to, String from, final String gameId) {
        if (to.getIsAndroid()) {
            JSONObject json = buildJsonAndroid(gameId, to);
            //send them a data payload
            sendNotification(json);
        }
        else { //if iOS user
            //get user so we can display name in notification
            FirebaseResourceManager.retrieveSingleNoUpdates(Constants.USER_PATH + "/" + from,
                    new ResourceListener<User>() {
                        @Override
                        public void onData(User inviter) {
                            JSONObject json = buildJsonIOS(gameId, to, inviter);
                            //send them a notification payload
                            sendNotification(json);
                        }

                        @Override
                        public Class getDataType() {
                            return User.class;
                        }
                    });
        }
    }

    private static JSONObject buildJsonAndroid(String gameId, User to) {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_TO, to.getNotificationId());
            JSONObject data = new JSONObject();
            data.put(NotificationReceiver.GAME_ID_KEY, gameId);
            data.put(NotificationReceiver.USER_ID_KEY, FirebaseResourceManager.getUserId());
            json.put(JSON_DATA, data);
            return json;
        } catch (JSONException err) {
            err.printStackTrace();
            Log.e("FIREBASE_UPLOADER", "Failed to create Android JSON " + err.getMessage());
        }
        return json;
    }

    private static JSONObject buildJsonIOS(String gameId, User to, User from) {
        //build notification key-value
        JSONObject notification = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            //add gameId and userId to notification
            notification.put(NotificationReceiver.USER_ID_KEY, from);
            notification.put(NotificationReceiver.GAME_ID_KEY, gameId);
            //add title and body to notification
            notification.put(JSON_BODY, String.format(IOS_NOTIFICATION_BODY, from.getDisplayName()));
            notification.put(JSON_TITLE, IOS_NOTIFICATION_TITLE);
            //add notification json to json
            json.put(JSON_NOTIFICATION_KEY, notification);
            //set priority high
            json.put(JSON_PRIORITY_KEY, JSON_PRIORITY_VAL);
            //set badge enabled
            json.put(JSON_BADGE_KEY, JSON_BADGE_VAL);
        }
        catch (JSONException err) {
            FirebaseCrash.log(err.getMessage());
            Log.e("NOTIFICATION", "Failed to create IOS JSON " + err.getMessage());
        }
        return json;
    }

    private static void sendNotification(final JSONObject json) {
        //run each notification on separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL firebaseMessageUrl = new URL(FIREBASE_MESSAGE_URL);
                    final HttpURLConnection connection =(HttpURLConnection)firebaseMessageUrl.openConnection();
                    connection.setRequestMethod(POST);
                    connection.setDoOutput(true);
                    connection.setRequestProperty(JSON_CONTENT_TYPE, JSON_CONTENT_VAL);
                    connection.setRequestProperty(JSON_AUTH,  JSON_AUTH_KEY + FIREBASE_SERVER_KEY);
                    final DataOutputStream write = new DataOutputStream(connection.getOutputStream());
                    write.writeBytes(json.toString());
                    write.flush();
                    write.close();
                    connection.connect();
                    Log.d("NOTIFICATION","Send message response msg: " + connection.getResponseMessage());
                }
                catch (MalformedURLException err) {
                    err.printStackTrace();
                    Log.e("NOTIFICATION", "Failed to create URL " + err.getMessage());
                }
                catch (IOException err) {
                    err.printStackTrace();
                    Log.e("NOTIFICATION", "Failed to write JSON to firebase " + err.getMessage());
                }
            }
        }).start();
    }


}
