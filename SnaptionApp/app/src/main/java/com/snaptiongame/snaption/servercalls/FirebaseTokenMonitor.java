package com.snaptiongame.snaption.servercalls;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * The registration token may change when:
 *
 * The app deletes Instance ID
 * The app is restored on a new device
 * The user uninstalls/reinstall the app
 * The user clears app data.
 *
 * This class is needed because the token changes in these cases
 * so to ensure users can consistently get notifications we need
 * to make sure the onTokenRefresh method updates the token in
 * the database
 *
 * Created by austinrobarts on 2/28/17.
 */

public class FirebaseTokenMonitor extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        //get the current token
        String token = FirebaseInstanceId.getInstance().getToken();
        //replace token in firebase with this token
        String userId = FirebaseResourceManager.getUserId();
        FirebaseUploader.updateUserNotificationToken(userId, token);
    }
}
