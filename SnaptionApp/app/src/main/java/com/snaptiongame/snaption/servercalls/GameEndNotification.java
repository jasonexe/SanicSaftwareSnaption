package com.snaptiongame.snaption.servercalls;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.ui.games.GameActivity;

/**
 * Created by austinrobarts on 5/4/17.
 */

public class GameEndNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent delayedIntent) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //get game Id from intent
        String gameId = delayedIntent.getStringExtra(GameActivity.USE_GAME_ID);
        //get picker's name from intent
        String pickerName = delayedIntent.getStringExtra(Constants.PICKER);
        //get game's acces
        String access = delayedIntent.getStringExtra(GameActivity.USE_GAME_ACCESS);
        intent.putExtra(GameActivity.USE_GAME_ID, gameId);
        intent.putExtra(GameActivity.USE_GAME_ACCESS, access);
        //create fake history so back button goes to Wall
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(GameActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //create notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_snaption)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(String.format(context.getResources().getString(R.string.game_ended_notification), pickerName))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        //display notification to user screen
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());

    }
}
