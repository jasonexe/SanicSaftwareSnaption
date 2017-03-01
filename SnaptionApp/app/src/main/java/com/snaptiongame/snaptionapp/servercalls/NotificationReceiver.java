package com.snaptiongame.snaptionapp.servercalls;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.util.Map;

/**
 * All notifications will be received by this method except:
 *
 * 1) Notifications delivered when your app is in the background.
 * In this case, the notification is delivered to the device’s system tray.
 * A user tap on a notification opens the app launcher by default.
 *
 * 2) Messages with both notification and data payload, both background and
 * foreground. In this case, the notification is delivered to the device’s
 * system tray, and the data payload is delivered in the extras of the intent
 * of your launcher Activity.
 *
 *  These two cases above will have to be dealt with when the main activity is
 *  launched and the message is stored as information in the intent
 *
 * Created by austinrobarts on 2/28/17.
 */

public class NotificationReceiver extends FirebaseMessagingService {

    private static final String gameIdKey = "gameId";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //message received in foreground
        Map<String, String> data = remoteMessage.getData();
        String gameId = null;

        //if a data message was received
        if (data != null && data.size() > 0) {
            //create intent to open up game from remoteMessage info
            gameId = data.get(gameIdKey);
        }

        //if a notification was received
        if (remoteMessage.getNotification() != null) {
            //not sure if needed yet
        }

        if (gameId != null) {
            FirebaseResourceManager.retrieveSingleNoUpdates(FirebaseGameResourceManager.GAME_TABLE + "/" + gameId, new ResourceListener<Game>() {
                @Override
                public void onData(Game game) {
                    createNotification(game);
                }

                @Override
                public Class getDataType() {
                    return Game.class;
                }
            });
        }



    }

    private void createNotification(Game game) {
        //create intent to go to game given
        Intent intent = new Intent(this, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(WallViewAdapter.GAME, game);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.flag)
                .setContentTitle("FCM Message")
                .setContentText("You have been invited to this game! Yahooo")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}