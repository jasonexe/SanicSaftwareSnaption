package com.snaptiongame.snaptionapp.servercalls;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;

/**
 * Created by jason_000 on 2/18/2017.
 */

public class DeepLinkGetter {

    public static void checkIfDeepLink(final MainSnaptionActivity mainActivity) {
        GoogleApiClient deepLinkReceiver = new GoogleApiClient.Builder(mainActivity)
                .enableAutoManage(mainActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mainActivity.showPostLogDialog("You appear to not have internet");
                    }
                })
                .addApi(AppInvite.API)
                .build();

        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(deepLinkReceiver, mainActivity, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if(result.getStatus().isSuccess()) {
                                    //Extract deep link, then do stuff with it
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    mainActivity.launchIntentFromDeepLink(deepLink);
                                } else {
                                    Log.d("No invitation", "No deep link found");
                                }
                            }
                        }
                );
    }
}
