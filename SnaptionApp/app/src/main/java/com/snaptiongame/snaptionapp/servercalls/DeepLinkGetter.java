package com.snaptiongame.snaptionapp.servercalls;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.ui.games.GameActivity;

import static com.snaptiongame.snaptionapp.ui.games.GameActivity.USE_GAME_ID;

/**
 * Created by jason_000 on 2/18/2017.
 */

public class DeepLinkGetter {

    public static void checkIfDeepLink(final FragmentActivity activity) {
        GoogleApiClient deepLinkReceiver = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Snackbar.make(activity.getCurrentFocus(),
                                    activity.getResources().getString(R.string.no_internet),
                                    Snackbar.LENGTH_LONG)
                                .show();
                    }
                })
                .addApi(AppInvite.API)
                .build();

        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(deepLinkReceiver, activity, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if(result.getStatus().isSuccess()) {
                                    //Extract deep link, then do stuff with it
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    launchIntentFromDeepLink(deepLink, activity);
                                } else {
                                    Log.d("No invitation", "No deep link found");
                                }
                            }
                        }
                );
    }

    private static void launchIntentFromDeepLink(String deepLink, Activity activity) {
        FirebaseDeepLinkCreator.DeepLinkInfo info = FirebaseDeepLinkCreator.interpretDeepLinkString(deepLink);
        // If we actually got back some info, we'll be launching another activity. Otherwise stay.
        if(info != null) {
            Class toLaunch = info.getClassForIntent();
            if(toLaunch == GameActivity.class) {
                Intent launchIntent = new Intent(activity, toLaunch);
                launchIntent.putExtra(USE_GAME_ID, info.getIntentString());
                activity.startActivity(launchIntent);
            }
        }
    }
}
