package com.snaptiongame.snaption.servercalls;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.GameData;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.ui.games.GameActivity;

import java.util.Map;

import static com.snaptiongame.snaption.Constants.*;

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

    public static final String GAME_ID_KEY = "gameId";
    public static final String USER_ID_KEY = "userId";
    public static final String GAME_ACCESS_KEY = "gameAccess";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //message received in foreground
        Map<String, String> data = remoteMessage.getData();
        String gameId = null;
        String senderUserId = null;
        String access = null;

        //if a data message was received
        if (data != null && data.size() > 0) {
            //create intent to open up game from remoteMessage info
            gameId = data.get(GAME_ID_KEY);
            senderUserId = data.get(USER_ID_KEY);
            access = data.get(GAME_ACCESS_KEY);

            createNotification(gameId, senderUserId, access);
        }

        //if a notification was received
        if (remoteMessage.getNotification() != null) {
            //not sure if needed yet
        }
    }

    private void createNotification(final String gameId, final String senderUserId, final String access) {
        //gets given game and given user to populate notification
        ResourceListener<GameMetadata> gameListener = new ResourceListener<GameMetadata>() {
            @Override
            public void onData(final GameMetadata metaData) {
                //after getting game, must get user
                FirebaseUserResourceManager.getUserMetadataById(senderUserId,
                    new ResourceListener<UserMetadata>() {
                        @Override
                        public void onData(UserMetadata user) {
                            //ensure the user and game were found before sending notification
                            if (metaData != null && user != null) {
                                sendNotification(metaData, user);
                            }
                        }

                        @Override
                        public Class getDataType() {
                            return UserMetadata.class;
                        }
                    });

            }
            @Override
            public Class getDataType() {
                return GameMetadata.class;
            }
        };
        //checking to make sure this data was in notification
        if (gameId != null && senderUserId != null) {
            FirebaseResourceManager.retrieveSingleNoUpdates(String.format(GAME_METADATA_PATH,
                    access, gameId), gameListener);
        }
    }

    private void sendNotification(GameMetadata game, UserMetadata user) {
        //create intent to go to game given
        Intent intent = new Intent(this, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GAME, game);
        //create fake history so back button goes to Wall
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(GameActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_snaption)
                .setContentTitle(getResources().getString(R.string.game_invite_notification_title))
                .setContentText(String.format(getResources().getString(R.string.game_invite_notification_text),
                        user.getDisplayName()))
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
